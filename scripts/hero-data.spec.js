'use strict'

// test dependencies
const dirtyChai = require('dirty-chai')
const chai = require('chai')
chai.use(dirtyChai)
const expect = chai.expect

// check if JSON file is available
const fs = require('fs')
const heroDataFile = '../app/src/main/res/raw/hero_stats_v1.json'

// exit if file doesn't exist
if (!fs.existsSync(heroDataFile)) {
  process.exit(0)
}

// tests
const heroData = require(heroDataFile)

describe('Hero Data v1 JSON', () => {
  it('contains heroes', () => {
    const minHeroes = 100
    expect(heroData.length > minHeroes).to.be.true()
    for (var i = 0; i < 5; i++) {
      const hero = heroData[i]
      expect(hero.title && hero.name &&
        hero.movementType && hero.weaponType &&
        hero.stats.length >= 0).to.be.true()
    }
  })

  it('playable heroes have weapons', () => {
    const alfonse = heroData.find((hero) => {
      return hero.name === 'Alfonse' && hero.title === 'Prince of Askr'
    })
    const alfonseHasWeaponStats = alfonse.stats.find((stat) => { return stat.equipped })
    expect(alfonseHasWeaponStats).to.be.not.undefined()

    const hana = heroData.find((hero) => {
      return hero.name === 'Hana' && hero.title === 'Focused Samurai'
    })
    const hanaHasWeaponStats = hana.stats.find((stat) => { return stat.equipped })
    expect(hanaHasWeaponStats).to.be.not.undefined()

    const lucina = heroData.find((hero) => {
      return hero.name === 'Lucina' && hero.title === 'Future Witness'
    })
    const lucinaHasWeaponStats = lucina.stats.find((stat) => { return stat.equipped })
    expect(lucinaHasWeaponStats).to.be.not.undefined()
  })

  it('heroes have different star ratings', () => {
    const sharena = heroData.find((hero) => {
      return hero.name === 'Sharena' && hero.title === 'Princess of Askr'
    })
    expect(sharena.stats.find((stat) => { return stat.rarity === 4 })).to.be.not.undefined()
    expect(sharena.stats.find((stat) => { return stat.rarity === 5 })).to.be.not.undefined()

    const shanna = heroData.find((hero) => {
      return hero.name === 'Shanna' && hero.title === 'Sprightly Flier'
    })
    expect(shanna.stats.find((stat) => { return stat.rarity === 3 })).to.be.not.undefined()
    expect(shanna.stats.find((stat) => { return stat.rarity === 4 })).to.be.not.undefined()
    expect(shanna.stats.find((stat) => { return stat.rarity === 5 })).to.be.not.undefined()
  })

  it('base and weapon stats are different', () => {
    const anna = heroData.find((hero) => {
      return hero.name === 'Anna' && hero.title === 'Commander'
    })
    const annaUnequipped = anna.stats.find((stat) => {
      return !stat.equipped && stat.rarity === 4
    })
    const annaEquipped = anna.stats.find((stat) => {
      return stat.equipped && stat.rarity === 4
    })
    expect(annaUnequipped.atk !== annaEquipped.atk).to.be.true()

    const tharja = heroData.find((hero) => {
      return hero.name === 'Tharja' && hero.title === 'Dark Shadow'
    })
    const tharjaUnequipped = tharja.stats.find((stat) => {
      return !stat.equipped && stat.rarity === 5
    })
    const tharjaEquipped = tharja.stats.find((stat) => {
      return stat.equipped && stat.rarity === 5
    })
    expect(tharjaUnequipped.atk !== tharjaEquipped.atk).to.be.true()
  })
})
