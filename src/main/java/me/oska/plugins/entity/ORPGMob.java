package me.oska.plugins.entity;

import lombok.Getter;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.converter.LocationConverter;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.orpg.Skill;
import me.oska.plugins.orpg.SkillType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@javax.persistence.Entity(name = "orpg_mob")
@Table(name = "orpg_mob")
public class ORPGMob {
    private static final Logger log = new Logger("ORPGMob");
    private static final AbstractRepository<ORPGMob> repository = new AbstractRepository<>(ORPGMob.class);
    private static final Map<String, ORPGMob> activeMobs = new HashMap<>();

    public static ORPGMob getByEntity(Entity entity) {
        return activeMobs.getOrDefault(entity.getUniqueId().toString(), null);
    }

    public static ORPGMob createMob(String mobId, Entity entity, Location location) {
        MobSetting setting = MobSetting.getMobSetting(mobId);
        ORPGMob mob = new ORPGMob();
        mob.uuid = entity.getUniqueId().toString();
        mob.location = location;
        mob.entity = entity;
        mob.setting_id = setting.getId();
        mob.strength = setting.getStrength();
        mob.accuracy = setting.getAccuracy();
        mob.dexterity = setting.getDexterity();
        mob.range = setting.getRange();
        mob.damage = setting.getDamage();
        mob.health = setting.getHealth();
        mob.skills = setting.getSkillIds().stream().map(ORPGSkill::getSkillById).collect(Collectors.toSet());

        activeMobs.put(mob.uuid, mob);
        repository.createAsync(mob, (e) -> log.toDiscord("Error occurs when creating mobs", e));
        return mob;
    }

    public void dispose() {
        repository.removeAsync(this, (e) -> log.toDiscord("Error occurs when removing mobs", e));
    }

    private ORPGMob() {}

    @Getter
    @Id
    private String uuid;

    @Getter
    @Convert(converter = LocationConverter.class)
    private Location location;

    @Getter
    private Integer strength;

    @Getter
    private Integer dexterity;

    @Getter
    private Integer accuracy;

    @Getter
    private Integer range;

    @Getter
    private Integer damage;

    @Getter
    private Integer health;

    @Getter
    @Transient
    private Entity entity;

    @Transient
    private Set<ORPGSkill> skills;

    @Getter
    private String setting_id;

    public MobSetting getSetting() {
        return MobSetting.getMobSetting(this.setting_id);
    }

    public Stream<Skill> getTriggerSkills(SkillType type) {
        return skills.parallelStream().filter(x -> x.getSkill().trigger(this, type)).map(ORPGSkill::getSkill);
    }
}
