package me.oska.plugins.mobs;

import io.lumine.xikage.mythicmobs.MythicMobs;
import lombok.Getter;
import lombok.Setter;
import me.oska.plugins.BaseEntity;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.skill.ORPGSkill;
import me.oska.plugins.skill.Skill;
import me.oska.plugins.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ORPGMob implements BaseEntity {
    private static Logger log;
    private static AbstractRepository<MobSetting> repository;
    private static Map<String, ORPGMob> activeMobs;
    private static Map<String, MobSetting> registeredMobs = new HashMap<>();
    private static MythicMobs mm;

    public static void register(JavaPlugin plugin) {
        log = new Logger("ORPGMob");
        repository = new AbstractRepository<>(MobSetting.class);
        activeMobs = new HashMap<>();
        registeredMobs = new HashMap<>();
        mm = MythicMobs.inst();

        try {
            repository.findAll().forEach(x -> {
                if (mm.getMobManager().getMythicMob(x.getId()) == null) {
                    log.toConsole("Failed to register mobs - " + x.getId());
                }
                registeredMobs.put(x.getId(), x);
            });
        } catch (RunicException e) {
            e.printStackTrace();
        }
    }

    public static ORPGMob getByEntity(Entity entity) {
        return activeMobs.getOrDefault(entity.getUniqueId().toString(), null);
    }

    public static ORPGMob createMob(String mobId, LivingEntity entity, Location location) {
        MobSetting setting = registeredMobs.getOrDefault(mobId, null);
        ORPGMob mob = new ORPGMob();
        mob.uuid = entity.getUniqueId().toString();
        mob.location = location;
        mob.entity = entity;
        mob.setting = setting;
        mob.strength = setting.getStrength();
        mob.accuracy = setting.getAccuracy();
        mob.dexterity = setting.getDexterity();
        mob.range = setting.getRange();
        mob.damage = setting.getDamage();
        mob.health = setting.getHealth();
        mob.skills = setting.getSkills().stream().map(ORPGSkill::getSkillById).collect(Collectors.toSet());
        activeMobs.put(mob.uuid, mob);
        return mob;
    }

    public void dispose() {
        activeMobs.remove(this.uuid);
    }

    private ORPGMob() {}

    @Getter
    @Id
    private String uuid;

    @Getter
    private Location location;

    @Getter
    private int strength;

    @Getter
    private int dexterity;

    @Getter
    private int accuracy;

    @Getter
    private int range;

    @Getter
    private int damage;

    @Getter
    private int maxHealth;

    @Getter
    @Setter
    private int health;

    @Getter
    private LivingEntity entity;

    private Set<ORPGSkill> skills;

    @Getter
    private MobSetting setting;

    public Stream<Skill> getTriggerSkills(SkillType type) {
        return skills.parallelStream().filter(x -> x.getSkill().trigger(this, type)).map(ORPGSkill::getSkill);
    }
}
