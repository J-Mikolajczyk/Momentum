import Set from '../models/Set';

export default function ExerciseCard ({blockData, exercise, exerciseIndex, update, weekNum, dayNum}){

    const handleAddSet = (exerciseIndex) => {
        blockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets.push(new Set());
        update();
    }

    const handleSetChange = (exerciseIndex, setIndex, field, value) => {
        const updatedBlockData = { ...blockData };
        updatedBlockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets[setIndex][field] = value;
        update()
    }

    const handleSetDelete = (exerciseIndex, setIndex) => {
        const updatedBlockData = { ...blockData };
        updatedBlockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets.splice(setIndex, 1);

        if (updatedBlockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets.length === 0) {
            updatedBlockData.weeks[weekNum].days[dayNum].exercises.splice(exerciseIndex, 1);
        }
        update()
    }

        return <div className="mb-6 p-4 border border-blue-800 rounded-md shadow-md w-full">
                        <h2 className="text-blue-800 font-anton text-2xl mb-2">{exercise?.name}</h2>

                        {exercise.sets?.map((set, setIndex) => (
                            <div key={setIndex} className="flex flex-row gap-4 mb-2">
                                <div>
                                    <label className="text-md font-anton">Weight:</label>
                                    <input
                                        type="number"
                                        className="ml-1 border rounded px-2 py-1 w-1/2 font-anton"
                                        value={set.weight}
                                        onBlur={(e) => handleSetChange(exerciseIndex, setIndex, 'weight', e.target.value)}
                                    />
                                </div>
                                <div>
                                    <label className="text-md font-anton">Reps:</label>
                                    <input
                                        type="number"
                                        className="ml-1 border rounded px-2 py-1 w-1/2 font-anton"
                                        value={set.reps}
                                        onBlur={(e) => handleSetChange(exerciseIndex, setIndex, 'reps', e.target.value)}
                                    />
                                </div>
                                <button onClick={(e) => handleSetDelete(exerciseIndex, setIndex)} className='font-anton'>X</button>
                            </div>
                        ))}

                        <button
                            onClick={() => handleAddSet(exerciseIndex)}
                            className="bg-blue-800 text-white px-4 py-1 rounded shadow-md mt-2 font-anton"
                        >
                            Add Set
                        </button>
                    </div>}