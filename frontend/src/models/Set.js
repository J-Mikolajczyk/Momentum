export default class Set {

    constructor(weight = 0.0, reps = 0) {
        this.weight = weight;
        this.reps = reps;
    }

    getWeight() {
        return this.weight;
    }

    setWeight(weight) {
        this.weight = weight;
    }

    getReps() {
        return this.reps;
    }

    setReps(reps) {
        this.reps = reps;
    }
}