export default class Set {

    constructor(weight = 0.0, reps = 0, logged = false) {
        this.weight = weight;
        this.reps = reps;
        this.logged = false;
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

    getLogged() {
        return this.logged;
    }

    setLogged(logged) {
        this.logged = logged;
    }
}