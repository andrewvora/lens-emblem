'use strict'

// test dependencies
const dirtyChai = require('dirty-chai')
const chai = require('chai')
chai.use(dirtyChai)
const expect = chai.expect

// class under test
const weaponModifiers = require('./weapon-modifiers')

// scenarios
describe('Weapon Modifiers', () => {
  it('parses names with apostrophes', () => {
    const weapon = { name: "Ayra's Blade" }
    const stats = { spd: 0 }

    weaponModifiers.apply(weapon, stats)

    expect(stats.spd).to.equal(3)
  })

  it('removes illegal characters', () => {
    const weapon = { name: 'Múspell Fireposy' }
    const stats = { spd: 0 }

    weaponModifiers.apply(weapon, stats)

    expect(stats.spd).to.equal(3)
  })

  it('all weapon mods are valid', () => {
    const acceptableStats = ['hp', 'atk', 'spd', 'def', 'res']
    const mods = weaponModifiers.weaponMods
    for (const key in mods) {
      const value = mods[key]
      expect(acceptableStats.indexOf(value.stat) >= 0).to.be.true()
      expect(value.mod).to.be.a('number')
    }
  })
})
