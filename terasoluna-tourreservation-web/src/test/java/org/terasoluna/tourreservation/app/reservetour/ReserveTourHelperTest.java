/*
 * Copyright (C) 2013-2015 terasoluna.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.terasoluna.tourreservation.app.reservetour;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.terasoluna.tourreservation.domain.model.Accommodation;
import org.terasoluna.tourreservation.domain.model.Arrival;
import org.terasoluna.tourreservation.domain.model.Customer;
import org.terasoluna.tourreservation.domain.model.Departure;
import org.terasoluna.tourreservation.domain.model.TourInfo;
import org.terasoluna.tourreservation.domain.service.reserve.ReserveService;
import org.terasoluna.tourreservation.domain.service.reserve.ReserveTourInput;
import org.terasoluna.tourreservation.domain.service.reserve.ReserveTourOutput;
import org.terasoluna.tourreservation.domain.service.tourinfo.PriceCalculateOutput;
import org.terasoluna.tourreservation.domain.service.tourinfo.PriceCalculateSharedSerivce;
import org.terasoluna.tourreservation.domain.service.tourinfo.TourInfoSharedService;
import org.terasoluna.tourreservation.domain.service.userdetails.ReservationUserDetails;

public class ReserveTourHelperTest {
    ReserveTourHelper reserveHelper;

    TourInfoSharedService tourInfoSharedService;

    ReserveService reserveService;

    PriceCalculateSharedSerivce priceCalculateSharedSerivce;

    TourInfo tourInfo;

    Customer customer;

    PriceCalculateOutput priceCalculateOutput;

    SecurityContext securityContext;

    ReservationUserDetails userDetails;

    User user;

    Mapper dozerBeanMapper;

    @Before
    public void setUp() throws Exception {
        SecurityContextHolder.clearContext();

        reserveHelper = new ReserveTourHelper();
        tourInfoSharedService = mock(TourInfoSharedService.class);
        reserveService = mock(ReserveService.class);
        priceCalculateSharedSerivce = mock(PriceCalculateSharedSerivce.class);

        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();

        reserveHelper.tourInfoSharedService = tourInfoSharedService;
        reserveHelper.reserveService = reserveService;
        reserveHelper.priceCalculateService = priceCalculateSharedSerivce;
        reserveHelper.dozerBeanMapper = dozerBeanMapper;

        // setup mock behavior
        String tourCode = "xxxxx";

        tourInfo = new TourInfo();
        tourInfo.setTourCode(tourCode);
        tourInfo.setBasePrice(10000);
        Arrival a = new Arrival();
        a.setArrCode("1234");
        tourInfo.setArrival(a);

        Departure departure = new Departure();
        departure.setDepCode("5678");
        tourInfo.setDeparture(departure);

        Accommodation accommodation = new Accommodation();
        accommodation.setAccomCode("9012");
        tourInfo.setAccommodation(accommodation);

        when(tourInfoSharedService.findOneWithDetails(tourCode)).thenReturn(tourInfo);

        priceCalculateOutput = new PriceCalculateOutput();
        priceCalculateOutput.setSumPrice(100000);
        priceCalculateOutput.setAdultCount(1);
        priceCalculateOutput.setChildCount(2);
        when(priceCalculateSharedSerivce.calculatePrice(10000, 1, 2))
                .thenReturn(priceCalculateOutput);

        customer = new Customer("12345678");
        
        userDetails = new ReservationUserDetails(customer);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testFindTourDetail01() {
        // test when principal is not null

        String tourCode = "xxxxx";

        ReserveTourForm form = new ReserveTourForm();
        form.setTourCode(tourCode);
        form.setAdultCount(1);
        form.setChildCount(2);

        // run
        TourDetailOutput resultOutput = reserveHelper.findTourDetail(userDetails, form);

        // assert
        assertThat(resultOutput.getCustomer(), is(customer));
        assertThat(resultOutput.getPriceCalculateOutput(),
                is(priceCalculateOutput));
        assertThat(resultOutput.getTourInfo(), is(tourInfo));
    }

    @Test
    public void testFindTourDetail02() {
        // test when principal is null

    	String tourCode = "xxxxx";

        ReserveTourForm form = new ReserveTourForm();
        form.setTourCode(tourCode);
        form.setAdultCount(1);
        form.setChildCount(2);

        // run
        TourDetailOutput resultOutput = reserveHelper.findTourDetail(null, form);

        // assert
        assertThat(resultOutput.getCustomer(), is(nullValue()));
        assertThat(resultOutput.getPriceCalculateOutput(),
                is(priceCalculateOutput));
        assertThat(resultOutput.getTourInfo(), is(tourInfo));
    }

    @Test
    public void testReserve01() {

        ReserveTourForm form = new ReserveTourForm();
        ReserveTourOutput output = new ReserveTourOutput();
        when(reserveService.reserve((ReserveTourInput) anyObject()))
                .thenReturn(output);

        // run
        ReserveTourOutput result = reserveHelper.reserve(userDetails, form);

        // assert
        assertThat(result, is(output));
    }

}
