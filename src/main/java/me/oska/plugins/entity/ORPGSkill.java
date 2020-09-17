package me.oska.plugins.entity;

import me.oska.minecraft.OskaRPG;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.orpg.Skill;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

@Entity(name = "orpg_skill")
@Table(name = "orpg_skill")
public class ORPGSkill extends BaseEntity {
    private static Map<String, ORPGSkill> skills = new HashMap<>();
    private static AbstractRepository<ORPGSkill> repository = new AbstractRepository<>(ORPGSkill.class);

    public static ORPGSkill getSkillById(String skillId) {
        return skills.getOrDefault(skillId, null);
    }

    public static void register(JavaPlugin plugin) {
        Runnable tick = () -> skills.values().parallelStream().filter(x -> x.skill.tick()).forEach(x -> x.skill.onTick());
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, tick, 0, 20);
    }

    @Id
    private String id;
    private String display;

    @Column(name = "file_name")
    private String fileName;    // TestSkill.class ( bytecode )

    @Column(name = "package_name")
    private String packageName; // me.oska.extension.TestSkill

    @Transient
    private Skill skill;

    public Skill getSkill() {
        return skill;
    }

    private void loadSkill() throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException, IOException {
        File file = new File(OskaRPG.getSkillFolder(),fileName +".class");
        if (!file.exists()) {
            throw new ClassNotFoundException("Skill '" + fileName + "' not found in skills directory");
        }

        URL url = new URL("file:" + file.getPath());
        URL[] urls = new URL[]{url};
        try (URLClassLoader loader = URLClassLoader.newInstance(urls, OskaRPG.getInstance().getClass().getClassLoader())) {
            Class clazz = loader.loadClass(packageName);
            if (!Skill.class.isAssignableFrom(clazz)){
                throw new InvalidClassException("Skill '" + fileName + "' doesn't extend me.oska.plugins.orpg.Skill");
            }
            Class<Skill> sc = clazz.asSubclass(Skill.class);
            Constructor<Skill> cst = sc.getConstructor();
            skill = cst.newInstance();
            skills.put(this.id, this);
        }
    }
}
