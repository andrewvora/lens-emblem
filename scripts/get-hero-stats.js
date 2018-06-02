'use strict'

const request = require('request')
const cheerio = require('cheerio')
const fs = require('fs')
const weaponModifiers = require('./weapon-modifiers')
const gamepediaUrl = 'https://feheroes.gamepedia.com'
const heroListUrl = `${gamepediaUrl}/Hero_List`
const outputDir = '../app/src/main/res/raw/'
const outputFile = 'hero_stats_v1.json'

/**
 * @return Promise that resolves to HTML from the given URL
 */
function getHtml(url) {
    return new Promise((resolve, reject) => {
        console.log('Fetching HTML from', url)
        request(url, (err, res, body) => {
            if (err) {
                console.log('Error fetching HTML from', url)
                reject(err)
            } else {
                resolve(body)
            }
        })
    })
}

/**
 * Modifies hero object with stat modifiers from
 * a weapon effect.
 */
async function applyEffectModifiers(weapon, stats) {
    weaponModifiers.apply(weapon, stats)
}

/**
 * Gets weapon info and applies modifiers to the given
 * hero object.
 * @return the modified hero object
 */
async function getWeaponStats(html) {
    const $ = cheerio.load(html)
    const weaponStats = []

    const weaponsTable = $('#Weapons').parent()
        .next('table.wikitable.default')
        .find('tbody')
        .first()
    const weaponStatsRows = weaponsTable.find('tr')
    const numRows = weaponStatsRows.length

    for (var i = 1; i < numRows; i++) {
        const row = $(weaponStatsRows[i])

        const name = row.find('td:nth-child(1)').text()
        const might = parseInt(row.find('td:nth-child(2)').text())
        const range = parseInt(row.find('td:nth-child(3)').text())
        const effect = row.find('td:nth-child(4)').text()
        const cost = parseInt(row.find('td:nth-child(5)').text())
        const defaultRarity = parseInt(row.find('td:nth-child(6)')
            .remove('span')
            .text())

        weaponStats.push({
            'name': name,
            'might': might,
            'range': range,
            'effect': effect,
            'cost': cost,
            'default_rarity': defaultRarity
        })
    }

    return weaponStats
}

/**
 * @return Promise that resolves to array with additional stats
 * that applied weapon modifiers
 */
async function getHeroStatsWithWeapon(html, heroStats) {
    const weaponStats = await getWeaponStats(html)
    const heroWithWeaponStats = []
    for (var i = 0; i < heroStats.length; i++) {
        const stats = heroStats[i]

        for (var j = 0; j < weaponStats.length; j++) {
            const weapon = weaponStats[j]

            if (stats['rarity'] == weapon['default_rarity']) {
                const clonedStats = JSON.parse(JSON.stringify(stats))
                clonedStats['equipped'] = true

                // apply preliminary modifiers
                const atk = parseInt(clonedStats['atk'])
                const atkMod = parseInt(weapon['might'])
                clonedStats['atk'] = atk + atkMod

                // apply weapon effects
                applyEffectModifiers(weapon, clonedStats)

                // finish processing for this stat set
                heroWithWeaponStats.push(clonedStats)
                break
            }
        }
    }

    return heroWithWeaponStats
}

/**
 * @return Promise that resolves to an array of Hero base stats.
 */
async function getHeroBaseStats(html, name, title) {
    const $ = cheerio.load(html)
    const stats = []

    // get base stats
    const baseStatsTable = $('#Level_1_stats').parent()
        .next('table.wikitable.default')
        .find('tbody')
        .first()
    const baseStatsRows = baseStatsTable.find('tr')
    const numBaseRows = baseStatsRows.length

    for (var i = 1; i < numBaseRows; i++) {
        const row = $(baseStatsRows[i])

        // rarity
        const rarityText = row.find('td:nth-child(1)')
            .remove('img')
            .text()
        const rarity = parseInt(rarityText)
        // hp
        const hpText = row.find('td:nth-child(2)').text().split('/')
        // use first part if hero doesn't have IVs
        const hp = parseInt(hpText.length == 1 ? hpText[0] : hpText[1])
        // atk
        const atkText = row.find('td:nth-child(3)').text().split('/')
        const atk = parseInt(atkText.length == 1 ? atkText[0] : atkText[1])
        // spd
        const spdText = row.find('td:nth-child(4)').text().split('/')
        const spd = parseInt(spdText.length == 1 ? spdText[0] : spdText[1])
        // def
        const defText = row.find('td:nth-child(5)').text().split('/')
        const def = parseInt(defText.length == 1 ? defText[0] : defText[1])
        // res
        const resText = row.find('td:nth-child(6)').text().split('/')
        const res = parseInt(resText.length == 1 ? resText[0] : resText[1])

        // create hero object
        stats.push({
            'level': 1,
            'rarity': rarity,
            'hp': hp,
            'atk': atk,
            'spd': spd,
            'def': def,
            'res': res,
            'sp': 0,
            'hm': 0,
            'equipped': false
        })
    }

    return stats
}

async function getHeroMaxStats(html, name, title) {
    const $ = cheerio.load(html)
    const stats = []

    const maxStatsTable = $('#Level_40_stats').parent()
        .next('table.wikitable.default')
        .find('tbody')
        .first()
    const maxStatsRows = maxStatsTable.find('tr')
    const numMaxRows = maxStatsRows.length

    for (var i = 1; i < numMaxRows; i++) {
        const row = $(maxStatsRows[i])

        // rarity
        const rarityText = row.find('td:nth-child(1)')
            .remove('img')
            .text()
        const rarity = parseInt(rarityText)
        // hp
        const hpText = row.find('td:nth-child(2)').text().split('/')
        const hp = parseInt(hpText.length == 1 ? hpText[0] : hpText[1])
        // atk
        const atkText = row.find('td:nth-child(3)').text().split('/')
        const atk = parseInt(atkText.length == 1 ? atkText[0] : atkText[1])
        // spd
        const spdText = row.find('td:nth-child(4)').text().split('/')
        const spd = parseInt(spdText.length == 1 ? spdText[0] : spdText[1])
        // def
        const defText = row.find('td:nth-child(5)').text().split('/')
        const def = parseInt(defText.length == 1 ? defText[0] : defText[1])
        // res
        const resText = row.find('td:nth-child(6)').text().split('/')
        const res = parseInt(resText.length == 1 ? resText[0] : resText[1])

        // create hero object
        stats.push({
            'level': 40,
            'rarity': rarity,
            'hp': hp,
            'atk': atk,
            'spd': spd,
            'def': def,
            'res': res,
            'sp': 0,
            'hm': 0,
            'equipped': false
        })
    }

    return stats
}

/**
 * @return Promise that resolves to an array of Hero objects
 */
async function getHeroStats(url, name, title) {
    console.log(`Fetching stats for '(${title}) ${name}'`)

    const html = await getHtml(url)
    var stats = []

    // get base stats
    const baseStats = await getHeroBaseStats(html, name, title)
    stats = stats.concat(baseStats)

    // get max stats
    const maxStats = await getHeroMaxStats(html, name, title)
    stats = stats.concat(maxStats)

    // get weapon modded base stats
    const baseWeaponStats = await getHeroStatsWithWeapon(html, baseStats)
    stats = stats.concat(baseWeaponStats)

    // get weapon modded max stats
    const maxWeaponStats = await getHeroStatsWithWeapon(html, maxStats)
    stats = stats.concat(maxWeaponStats)

    return {
        'title': title,
        'name': name,
        'stats': stats
    }
}

/**
 * @return Promise that resolves to array of hero objects
 */
async function getHeroInfo(html) {
    const $ = cheerio.load(html)
    const rowItems = $('tr.hero-filter-element')
    var heroes = []

    for (var i = 0; i < rowItems.length; i++) {
    // for (var i = 0; i < 3; i++) {
        const item = $(rowItems[i])
        const heroNameItem = item.find('td:nth-child(2) > a')
        const heroUrl = `${gamepediaUrl}${heroNameItem.attr('href')}`
        const name = heroNameItem.text()
        const title = item.find('td:nth-child(3)').text()

        const stats = await getHeroStats(heroUrl, name, title)
        const heroImgUrl = item.find('td:nth-child(1) img').attr('src')
        stats['imageUrl'] = heroImgUrl

        heroes = heroes.concat(stats)
    }

    return heroes
}

function writeHeroInfoToFile(heroes) {
    // console.log(JSON.stringify(heroes))
    return new Promise((resolve, reject) => {
        fs.writeFile(
            '../app/src/main/res/raw/hero_stats_v1.json',
            //'./hero_stats_v1.json',
            JSON.stringify(heroes),
            'utf8',
            (err, data) => {
                if (err) {
                    reject(err)
                } else {
                    resolve(data)
                }
            })
    })
}

// starts downloading hero stats and constructing hero objects
request(heroListUrl, (err, res, body) => {
    if (err) {
        console.log('Couldn\'t load hero list.\n', err)
    } else {
        getHeroInfo(body)
            .then((heroes) => {
                writeHeroInfoToFile(heroes)
                    .then((result) => {
                        console.log('Heroes downloaded!')
                    })
                    .catch((e) => {
                        console.log('Exception writing heroes to file.\n', e)
                    })
            })
            .catch((e) => {
                console.log('Exception while downloading heroes.\n', e)
            })
    }
})
