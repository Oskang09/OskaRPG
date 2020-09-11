package me.oska.extension;

import me.oska.plugins.ORPGPlayer;
import me.oska.plugins.orpg.Skill;
import me.oska.plugins.orpg.SkillType;

public class TestSkill extends Skill {

    @Override
    public boolean trigger(ORPGPlayer player, SkillType type) {
        return true;
    }

}
