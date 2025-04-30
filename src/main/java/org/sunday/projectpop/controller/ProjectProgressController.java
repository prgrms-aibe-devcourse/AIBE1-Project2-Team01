package org.sunday.projectpop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/onprojects")
@RequiredArgsConstructor
public class ProjectProgressController {
    //private Item item;
//    private final ItemRepository itemRepository;
//    private final CustomerRepository customerRepository;
//    private final OrderRepository orderRepository;
//    private final DetailedorderRepository detailedorderRepository;

    @GetMapping("")
    public String getShopPage() {
        return "onproject/index";  // 템플릿 이름
    }


    public String progressCalculation(Model model) {
        // 실제로는 데이터베이스나 서비스 로직을 통해 프로젝트 진행도를 가져와야 합니다.
        int onProjectProgress = getProjectProgress();

        model.addAttribute("onProjectProgress", onProjectProgress);
        return "dashboard"; // dashboard.html (또는 해당 템플릿 이름)
    }
}

