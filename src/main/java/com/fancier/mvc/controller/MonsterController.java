package com.fancier.mvc.controller;

import com.fancier.mvc.annotation.AutoWired;
import com.fancier.mvc.annotation.Controller;
import com.fancier.mvc.annotation.RequestMapping;
import com.fancier.mvc.annotation.RequestParam;
import com.fancier.mvc.entity.Monster;
import com.fancier.mvc.service.MonsterService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
@Controller
public class MonsterController {

    @AutoWired
    private MonsterService monsterService;

    @RequestMapping("/list/monster")
    public void getMonsterList(HttpServletResponse response) throws IOException {
        response.getWriter().println("<h1>monster-list</h1>");
        List<Monster> monsters = monsterService.monsterList();
        for (Monster monster : monsters) {
            response.getWriter().println(monster + "<br>");
        }
    }

    @RequestMapping("/get/monster")
    public void getMonster(@RequestParam("name") String name, HttpServletResponse response) throws IOException {
        response.getWriter().println("<h1>monster</h1>");
        List<Monster> monsters = monsterService.monsterList();
        for (Monster monster : monsters) {
            if (monster.getName().equals(name)) {
                response.getWriter().println(monster + "<br>");
            }
        }
    }

    @RequestMapping("/monster/login")
    public String login(String name, HttpServletRequest request) {
        Boolean isSuccess = monsterService.login(name);
        request.setAttribute("name", name);
        return isSuccess ? "forward:/login_ok.jsp" : "forward:/login_error.jsp";
    }
}
