package com.demo.jpastreamer.controller;

import com.demo.jpastreamer.persistance.Product;
import com.demo.jpastreamer.persistance.UserDomain;
import com.demo.jpastreamer.persistance.UserDomain$;
import com.demo.jpastreamer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class DemoController {

    private final UserService userService;


    @PostMapping("/demopp")
    public Product createUser(@RequestBody Product product) {

        return userService.createProduct(product);
    }
    @GetMapping("/demoget")
    public List<Product> getProducts() {

        return userService.getProducts();

    }

    @PostMapping("/demo")
    public UserDomain createUser(@RequestBody UserDomain userDomain) {

        return userService.createUser(userDomain);
    }

    @GetMapping("/demo")
    public List<UserDomain> getUsers() {
        userService.grouping();

        return userService.getUsers();

    }

    @GetMapping("/pages")
    public List<UserDomain> getPages() {
//        Comparator<UserDomain> customComparator = Comparator.comparing(UserDomain::getLastName)
//                .thenComparing(UserDomain::getFirstName);

          return userService.filmPage(0, UserDomain$.lastName.reversed());
    }

    @GetMapping("/manyone")
    public Map<UserDomain, Product> getManytoOne() {
       return userService.ManytoOne();
    }

    @GetMapping("/grouping")
    public Map<String, List<UserDomain>> getgrouping() {
        return userService.grouping();
    }



    @GetMapping("/transaction")
    public String getting() {
        return userService.transaction();
    }

}
