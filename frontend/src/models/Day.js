export default class Day {

    constructor(name = null, exercises = []) {
        this.name = name;
        this.exercises = exercises;
    }

    getExercises() {
        return this.exercises;
    }

    setExercises(exercises) {
        this.exercises = exercises;
    }

    getName() {
        return this.name;
    }

    setName(name) {
        this.name = name;
    }
}