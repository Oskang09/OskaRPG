package me.oska.plugins;

import me.oska.minecraft.OskaRPG;
import me.oska.plugins.openjpa.AbstractRepository;
import me.oska.plugins.orpg.Skill;
import me.oska.plugins.orpg.Stats;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table
public class ORPGSkill {
    private static Map<String, ORPGSkill> skills = new HashMap<>();
    private static AbstractRepository<ORPGSkill> repository = new AbstractRepository<>(ORPGSkill.class);

    public static String toSQL(List<ORPGSkill> skill) {
        return skill.stream().map(x -> x.id).collect(Collectors.joining(","));
    }

    public static List<ORPGSkill> fromSQL(String sqlArray) {
        List<ORPGSkill> instance = new ArrayList<>();
        if (sqlArray != null) {
            for (String skillId : sqlArray.split(",")) {
                instance.add(skills.get(skillId));
            }
        }
        return instance;
    }

    @Id
    private String id;
    private Skill skill;
    private String display;
    private String fileName;    // TestSkill.class ( bytecode )
    private String packageName; // me.oska.extension.TestSkill

    public Skill getSkill() {
        return skill;
    }

    private void loadSkill() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, IOException {
        File file = new File(OskaRPG.getSkillFolder(),fileName +".class");
        if (!file.exists()) {
            throw new ClassNotFoundException("Skill '" + fileName + "' not found in skills directory.");
        }

        URL url = new URL("file:" + file.getPath());
        URL[] urls = new URL[]{url};
        try (URLClassLoader loader = URLClassLoader.newInstance(urls, OskaRPG.getInstance().getClass().getClassLoader())) {
            Class clazz = loader.loadClass(packageName);
            if (!Skill.class.isAssignableFrom(clazz)){
                throw new InvalidClassException("Skill '" + fileName + "' doesn't extend me.oska.plugins.orpg.Skill.");
            }
            Class<Skill> sc = clazz.asSubclass(Skill.class);
            Constructor<Skill> cst = sc.getConstructor();
            skill = cst.newInstance();
            skills.put(this.id, this);
        }
    }
}