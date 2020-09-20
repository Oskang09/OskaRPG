package me.oska.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BannerUtil {

    private static List<ItemStack> numberBanners;

    public static ItemStack get(int number) {
        if (number < 0 || number > 9) {
            return new ItemStack(Material.AIR);
        }
        return numberBanners.get(number).clone();
    }

    public static void initialize() {
        numberBanners = new ArrayList<>();

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0ls0ts0rs0dls0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15cs0tl0cbo15bs0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_CENTER));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.SQUARE_TOP_LEFT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ts0mr15bs0dls0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0ms0ts0cbo15rs0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.CURLY_BORDER));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ls0hhb15rs0ms0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL_MIRROR));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0mr15ts0drs0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.RHOMBUS_MIDDLE));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNRIGHT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15bs0rs0hh15ms0ts0ls0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15dls0ts0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_DOWNLEFT));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ts0ls0ms0bs0rs0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));

        // https://www.gamergeeks.nz/apps/minecraft/banner-maker#mcb=a15ls0hhb15ms0ts0rs0bs0bo15
        numberBanners.add(build(() -> {
            List<Pattern> patterns = new ArrayList<>();
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.HALF_HORIZONTAL_MIRROR));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT));
            patterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
            patterns.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
            return patterns;
        }));
    }

    private static ItemStack build(Supplier<List<Pattern>> patterns) {
        ItemStack item = new ItemStack(Material.BLACK_BANNER);
        BannerMeta meta = (BannerMeta) Bukkit.getItemFactory().getItemMeta(Material.BLACK_BANNER);
        meta.setPatterns(patterns.get());
        item.setItemMeta(meta);
        return item;
    }
}
