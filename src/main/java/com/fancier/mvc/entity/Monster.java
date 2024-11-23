package com.fancier.mvc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/hola1009">fancier</a>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Monster {
    private Integer id;
    private String name;
    private Integer age;
}
