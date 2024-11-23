package com.fancier.mvc.service;

import com.fancier.mvc.entity.Monster;

import java.util.List;

/**
 * @author <a href="https://github.com/hola1009">fancier</a>
 */

public interface MonsterService {
    List<Monster> monsterList();

    Boolean login(String name);
}
