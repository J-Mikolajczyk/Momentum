export default class Week {

    constructor(days = []) {
        this.days = days;
    }

    getDays() {
        return this.days;
    }

    setDays(days) {
        this.days = days;
    }
}