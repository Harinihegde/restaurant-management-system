package com.restaurant.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerFacade customerFacade;

    public CustomerController(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    @GetMapping
    public String profile(Model model) {
        model.addAttribute("customers", customerFacade.getCustomersForView());
        model.addAttribute("customerForm", new CustomerProfile());
        return "customer";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("customerForm") CustomerProfile customer, RedirectAttributes redirectAttributes) {
        customerFacade.saveCustomer(customer);
        redirectAttributes.addFlashAttribute("successMessage", "Customer saved successfully.");
        return "redirect:/customer";
    }
}

@Component
class CustomerFacade {

    private final List<CustomerProfile> customers = new ArrayList<>();
    private final AtomicLong customerIds = new AtomicLong(1);

    CustomerFacade() {
        saveCustomer(new CustomerProfile(null, "Aarav Mehta", "aarav@example.com", "LOYALTY", 250));
    }

    List<CustomerProfile> getCustomersForView() {
        return new ArrayList<>(customers);
    }

    CustomerProfile saveCustomer(CustomerProfile customer) {
        customer.setId(customerIds.getAndIncrement());
        if (customer.getLoyaltyPoints() == null) {
            customer.setLoyaltyPoints(0);
        }
        customers.add(customer);
        return customer;
    }

    public String getBenefits(CustomerProfile customer) {
        if (customer.getMembershipType() == null || "BASIC".equalsIgnoreCase(customer.getMembershipType())) {
            return "Standard dining access";
        }
        return "Priority support and loyalty benefits";
    }
}

class CustomerProfile {

    private Long id;
    private String name = "";
    private String email = "";
    private String membershipType = "BASIC";
    private Integer loyaltyPoints = 0;

    CustomerProfile() {
    }

    CustomerProfile(Long id, String name, String email, String membershipType, Integer loyaltyPoints) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.membershipType = membershipType;
        this.loyaltyPoints = loyaltyPoints;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
