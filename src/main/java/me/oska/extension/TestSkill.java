package me.oska.extension;

import me.oska.plugins.ORPGPlayer;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.orpg.Skill;
import me.oska.plugins.orpg.SkillType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TestSkill extends Skill {

    public static Logger log = new Logger("Skill - TestSkill");

    @Override
    public void playerDamageEntity(EntityDamageByEntityEvent event, ORPGPlayer attacker, LivingEntity victim) {
        event.setDamage(100);
        log.toConsole("running", null);
    }

    @Override
    public boolean trigger(ORPGPlayer player, SkillType type) {
        return true;
    }

}
