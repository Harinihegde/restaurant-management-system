package com.restaurant.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping
public class AdminController {

    private final AdminModule adminModule;

    public AdminController(AdminModule adminModule) {
        this.adminModule = adminModule;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("dbStatus", DatabaseConnection.getInstance().getConnectionStatus());
        return "index";
    }

    @GetMapping("/admin")
    public String menuManagement(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("menus", adminModule.searchMenus(keyword));
        model.addAttribute("staff", adminModule.getStaff());
        model.addAttribute("keyword", keyword);
        model.addAttribute("menuForm", new MenuItem());
        return "admin-menu";
    }

    @PostMapping("/admin/save")
    public String saveMenuItem(@ModelAttribute("menuForm") MenuItem menu, RedirectAttributes redirectAttributes) {
        adminModule.saveMenu(menu);
        redirectAttributes.addFlashAttribute("successMessage", "Menu item saved successfully.");
        return "redirect:/admin";
    }
}

@Component
class AdminModule {

    private final List<MenuItem> menus = new ArrayList<>();
    private final List<StaffMember> staff = new ArrayList<>();
    private final AtomicLong menuIds = new AtomicLong(1);

    AdminModule() {
        menus.add(new MenuItem(menuIds.getAndIncrement(), "Paneer Tikka", "Starter", new BigDecimal("220.00"), true));
        menus.add(new MenuItem(menuIds.getAndIncrement(), "Veg Biryani", "Main Course", new BigDecimal("280.00"), true));
        staff.add(new StaffMember(1L, "Riya", "Chef"));
        staff.add(new StaffMember(2L, "Arjun", "Manager"));
    }

    List<MenuItem> searchMenus(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(menus);
        }
        String normalized = keyword.toLowerCase();
        return menus.stream()
                .filter(menu -> menu.getItemName().toLowerCase().contains(normalized)
                        || menu.getCategory().toLowerCase().contains(normalized))
                .toList();
    }

    List<StaffMember> getStaff() {
        return new ArrayList<>(staff);
    }

    void saveMenu(MenuItem menu) {
        menu.setId(menuIds.getAndIncrement());
        if (menu.getPrice() == null) {
            menu.setPrice(BigDecimal.ZERO);
        }
        menu.setAvailable(true);
        menus.add(menu);
    }
}

final class DatabaseConnection {

    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private final String connectionStatus;

    private DatabaseConnection() {
        this.connectionStatus = "Database connection singleton initialized";
    }

    static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    String getConnectionStatus() {
        return connectionStatus;
    }
}

class MenuItem {

    private Long id;
    private String itemName = "";
    private String category = "";
    private BigDecimal price = BigDecimal.ZERO;
    private boolean available = true;

    MenuItem() {
    }

    MenuItem(Long id, String itemName, String category, BigDecimal price, boolean available) {
        this.id = id;
        this.itemName = itemName;
        this.category = category;
        this.price = price;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

class StaffMember {

    private final Long id;
    private final String name;
    private final String role;

    StaffMember(Long id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
