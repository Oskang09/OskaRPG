package me.oska.plugins.orpg;

public enum SkillType {
    PLAYER_DAMAGE_PLAYER("P2P"),
    PLAYER_DAMAGE_ENTITY("P2E"),
    ENTITY_DAMAGE_PLAYER("E2P"),
    PLAYER_SHOOT_BLOCK("PSB"),
    PLAYER_SHOOT_ENTITY("PSE"),
    ENTITY_SHOOT_PLAYER("ESP"),
    PLAYER_SHOOT("PS");

    String type;
    SkillType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
