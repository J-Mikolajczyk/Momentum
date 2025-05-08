import Set from './Set'

export default class Exercise {

    constructor(name = null, sets = []) {
        this.name = name;
        this.sets = [...sets, new Set()];
    }

    getSets() {
        return this.sets;
    }

    setSets(sets) {
        this.sets = sets;
    }

    getName() {
        return this.name;
    }

    setName(name) {
        this.name = name;
    }
}