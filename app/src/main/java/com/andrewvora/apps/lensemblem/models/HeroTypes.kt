package com.andrewvora.apps.lensemblem.models

import com.google.gson.annotations.SerializedName

/**
 * Created on 7/7/2018.
 * @author Andrew Vorakrajangthiti
 */

enum class WeaponType {
    // reds
    @SerializedName("red_sword") RED_SWORD,
    @SerializedName("red_tome") RED_TOME,
    @SerializedName("red_bow") RED_BOW,
    @SerializedName("red_breath") RED_BREATH,

    // blues
    @SerializedName("blue_lance") BLUE_LANCE,
    @SerializedName("blue_tome") BLUE_TOME,
    @SerializedName("blue_bow") BLUE_BOW,
    @SerializedName("blue_breath") BLUE_BREATH,

    // greens
    @SerializedName("green_axe") GREEN_AXE,
    @SerializedName("green_tome") GREEN_TOME,
    @SerializedName("green_bow") GREEN_BOW,
    @SerializedName("green_breath") GREEN_BREATH,

    // colorless
    @SerializedName("colorless_bow") COLORLESS_BOW,
    @SerializedName("colorless_staff") COLORLESS_STAFF,
    @SerializedName("colorless_dagger") COLORLESS_DAGGER,
    @SerializedName("colorless_breath") COLORLESS_BREATH,

    UNKNOWN
}

enum class MovementType {
    @SerializedName("cavalry") CAVALRY,
    @SerializedName("infantry") INFANTRY,
    @SerializedName("flying") FLYING,
    @SerializedName("armored") ARMORED,

    UNKNOWN
}