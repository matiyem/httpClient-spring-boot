package com.example.web.controller;

import com.example.web.dto.Bar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by Atiye Mousavi
 * Date: 9/24/2021
 * Time: 4:12 PM
 */
@RestController
@RequestMapping(value = "/bars")
public class BarController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public BarController() {
        super();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Bar findOne(@PathVariable("id") final Long id){
        return new Bar();
    }
}
