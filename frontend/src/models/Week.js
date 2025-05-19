export default class Week {

    constructor(days = []) {
        this.days = days.map(day => ({
            ...day,
            exercises: day.exercises.map(exercise => ({
                ...exercise,
                sets: exercise.sets.map(() => ({ weight: '', reps: '', logged: false }))
            }))
        }));
    }

    getDays() {
        return this.days;
    }

    setDays(days) {
        this.days = days;
    }
}