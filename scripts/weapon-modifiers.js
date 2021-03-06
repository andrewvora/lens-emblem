'use strict'

const weaponMods = {
  // swords
  'amiti': { stat: 'spd', mod: -2 },
  'audhulma': { stat: 'res', mod: 5 },
  'ayra\'s blade': { stat: 'spd', mod: 3 },
  'beloved zofia': { stat: 'def', mod: 3 },
  'blazing durandal': { stat: 'atk', mod: 3 },
  'brave sword': { stat: 'spd', mod: -5 },
  'divine tyrfing': { stat: 'res', mod: 3 },
  'exalted falchion': { stat: 'spd', mod: 3 },
  'light brand': { stat: 'def', mod: 3 },
  'meisterschwert': { stat: 'spd', mod: -5 },
  'gjöll': { stat: 'atk', mod: 3 },
  'resolute blade': { stat: 'atk', mod: 3 },
  'royal sword': { stat: 'spd', mod: 3 },
  'laevatein': { stat: 'atk', mod: 3 },
  'light brand': { stat: 'def', mod: 3 },
  'exalted falchion': { stat: 'spd', mod: 3 },
  'níu': { stat: 'spd', mod: 3 },
  'niu': { stat: 'spd', mod: 3 },
  'resolute blade': { stat: 'atk', mod: 3 },
  'royal sword': { stat: 'atk', mod: 3 },
  'storm sieglinde': { stat: 'atk', mod: 3 },

  // red tomes
  'aversa\'s night' : { stat: 'res', mod: 3 },
  'book of dreams': { stat: 'atk', mod: 3 },
  'fruit of iðunn': { stat: 'spd', mod: 3 },
  'fruit of iounn': { stat: 'spd', mod: 3 },
  'gleipnir': { stat: 'res', mod: 3 },
  'grima\'s truth': { stat: 'def', mod: 3 },
  'imhullu': { stat: 'res', mod: 3 },
  'loptous': { stat: 'res', mod: 3 },
  'múspell fireposy': { stat: 'spd', mod: 3 },

  // lance
  'brave lance': { stat: 'spd', mod: -5 },
  'flame siegmund': { stat: 'atk', mod: 3 },
  'geirskögul': { stat: 'def', mod: 3 },

  // blue tomes
  'dire thunder': { stat: 'spd', mod: -5 },
  'huginn\'s egg': { stat: 'res', mod: 3 },
  'ivaldi': { stat: 'def', mod: 3 },
  'prayer wheel': { stat: 'spd', mod: 3 },
  'sagittae': { stat: 'def', mod: 3 },
  'thani': { stat: 'res', mod: 3 },
  'weirding tome': { stat: 'spd', mod: 3 },

  // axes
  'brave axe': { stat: 'spd', mod: -5 },
  'cherche\'s axe': { stat: 'spd', mod: -5 },
  'garm': { stat: 'atk', mod: 3 },
  'sinmara': { stat: 'def', mod: 3 },
  'thunder armads': { stat: 'def', mod: 3 },
  'wolf berg': { stat: 'def', mod: 3 },
  
  // green tomes
  'blizzard': { stat: 'res', mod: 3 },
  'book of shadows': { stat: 'spd', mod: 3 },
  'forseti': { stat: 'spd', mod: 3 },
  'giga excalibur': { stat: 'spd', mod: 3 },
  'muninn\'s egg': { stat: 'res', mod: 3 },
  'nifl frostflowers': { stat: 'atk', mod: 3 },

  // breaths
  'great flame': { stat: 'atk', mod: 3 },
  'spirit breath': { stat: 'def', mod: 3 },
  'summer\'s breath': { stat: 'def', mod: 3 },

  // bows
  'brave bow': { stat: 'spd', mod: -5 },
  'mulagir': { stat: 'spd', mod: 3 },
  'skadi': { stat: 'spd', mod: 3 },
  'swift mulagir': { stat: 'res', mod: 3 },
  'thögn': { stat: 'spd', mod: 3 },
  'warrior princess': { stat: 'spd', mod: 3 },

  // daggers
  'dawn suzu': { stat: 'atk', mod: 3 },
  'lyfjaberg': { stat: 'res', mod: 3 },
  'sylgr': { stat: 'spd', mod: 3 }
}

module.exports.weaponMods = weaponMods
module.exports.apply = function(weapon, stats) {
    const weaponName = weapon.name
        .toLowerCase()
        .trim()
        .replace('^[a-zA-Z\'\s]', '')

    const match = weaponMods[weaponName]
    if (match != undefined) {
        stats[match.stat] = stats[match.stat] + match.mod
    }
}
