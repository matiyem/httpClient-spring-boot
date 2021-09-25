package com.example.web.controller;

import com.example.web.dto.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * created by Atiye Mousavi
 * Date: 9/24/2021
 * Time: 5:53 PM
 */
@Controller
@RequestMapping(value = "/foos")
public class FooController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public FooController(){
        super();
    }

    public Foo findOne(@PathVariable("id") final Long id){
        return new Foo();
    }
}
