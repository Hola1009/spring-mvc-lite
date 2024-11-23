package com.fancier.mvc.service.impl;

import com.fancier.mvc.annotation.Service;
import com.fancier.mvc.entity.Monster;
import com.fancier.mvc.service.MonsterService;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
@Service
public class MonsterServiceImpl implements MonsterService {

    @Override
    public List<Monster> monsterList() {
        return Arrays.asList(new Monster(1, "monster1", 13),
                new Monster(2, "monster2", 23),
                new Monster(3, "monster3", 33));
    }

    @Override
    public Boolean login(String name) {
        return "monster1".equals(name);
    }
}
