package com.andrewvora.apps.lensemblem.dagger

import dagger.Module
import dagger.Provides
import javax.inject.Named

/**
 * Holds runtime constants.
 *
 * Created on 3/4/2018.
 * @author Andrew Vorakrajangthiti
 */
@Module
class ConstantsModule {
    companion object {
        const val NOTIFICATIONS_URL = "notification_json_url"
        const val HERO_STATS_URL = "hero_stats_json_url"
        const val HERO_ALIASES_URL = "hero_aliases_json_url"
    }

    @Provides
    @Named(NOTIFICATIONS_URL)
    fun providesNotificationsUrl(): String {
        return "https://raw.githubusercontent.com/andrewvora/lens-emblem/master/app/src/main/res/raw/notifications_v1.json"
    }

    @Provides
    @Named(HERO_STATS_URL)
    fun providesHeroStatsUrl(): String {
        return "https://raw.githubusercontent.com/andrewvora/lens-emblem/master/app/src/main/res/raw/hero_stats_v1.json"
    }

    @Provides
    @Named(HERO_ALIASES_URL)
    fun providesHeroAliasesUrl(): String {
        return "https://raw.githubusercontent.com/andrewvora/lens-emblem/master/app/src/main/res/raw/hero_name_aliases_v1.json.json"
    }
}