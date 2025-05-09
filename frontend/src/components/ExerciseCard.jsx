import { useState } from 'react';
import Set from '../models/Set';

export default function ExerciseCard({ blockData, exercise, exerciseIndex, update, weekNum, dayNum }) {
    const [inputValues, setInputValues] = useState(() => {
        return exercise.sets?.map(set => ({
            weight: set.weight || '',
            reps: set.reps || '',
        })) || [];
    });

    const handleLocalChange = (setIndex, field, value) => {
        const updatedInputs = [...inputValues];
        updatedInputs[setIndex] = { ...updatedInputs[setIndex], [field]: value };
        setInputValues(updatedInputs);
    };

    const handleBlur = (setIndex, field) => {
        const value = inputValues[setIndex][field];
        const updatedBlockData = { ...blockData };
        updatedBlockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].sets[setIndex][field] = value;
        update();
    };

    const handleAddSet = (exerciseIndex) => {
        const targetName = blockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].name;
      
        for (let i = weekNum; i < blockData.weeks.length; i++) {
          const day = blockData.weeks[i].days[dayNum];
          const matchingExercise = day.exercises.find(ex => ex.name === targetName);
      
          if (matchingExercise) {
            matchingExercise.sets.push(new Set());
          }
        }
      
        setInputValues(prev => [...prev, { weight: '', reps: '' }]);
        update();
      };
      
      

      const handleSetDelete = (exerciseIndex, setIndex) => {
        const updatedBlockData = { ...blockData };
        const targetName = updatedBlockData.weeks[weekNum].days[dayNum].exercises[exerciseIndex].name;
      
        for (let i = weekNum; i < updatedBlockData.weeks.length; i++) {
          const day = updatedBlockData.weeks[i].days[dayNum];
          const matchingExerciseIndex = day.exercises.findIndex(ex => ex.name === targetName);
      
          if (matchingExerciseIndex !== -1) {
            const exercise = day.exercises[matchingExerciseIndex];
      
            if (exercise.sets.length > setIndex) {
              exercise.sets.splice(setIndex, 1);
      
              if (exercise.sets.length === 0) {
                day.exercises.splice(matchingExerciseIndex, 1);
              }
            }
          }
        }
      
        const newInputValues = [...inputValues];
        newInputValues.splice(setIndex, 1);
        setInputValues(newInputValues);
      
        update();
      };
      
      

    return (
        <div className="p-2 pr-3 border border-blue-800 rounded-md shadow-md w-full">
            <h2 className="text-blue-800 font-anton text-xl mb-2">{exercise?.name}</h2>

            {exercise.sets?.map((set, setIndex) => (
                <div key={setIndex} className="flex flex-row gap-4 mb-2">
                    <div>
                        <label className="text-md font-anton">Weight:</label>
                        <input
                            type="number"
                            className="ml-1 border rounded px-2 py-1 w-1/2 font-anton h-6"
                            value={inputValues[setIndex]?.weight || ''}
                            onChange={(e) => handleLocalChange(setIndex, 'weight', e.target.value)}
                            onBlur={() => handleBlur(setIndex, 'weight')}
                        />
                    </div>
                    <div>
                        <label className="text-md font-anton">Reps:</label>
                        <input
                            type="number"
                            className="ml-1 border rounded px-2 py-1 w-1/2 font-anton h-6"
                            value={inputValues[setIndex]?.reps || ''}
                            onChange={(e) => handleLocalChange(setIndex, 'reps', e.target.value)}
                            onBlur={() => handleBlur(setIndex, 'reps')}
                        />
                    </div>
                    <button onClick={() => handleSetDelete(exerciseIndex, setIndex)} className='font-anton-no-italic'>X</button>
                </div>
            ))}

            <button
                onClick={() => handleAddSet(exerciseIndex)}
                className="bg-blue-800 text-white px-4 py-1 rounded shadow-md mt-2 font-anton"
            >
                Add Set
            </button>
        </div>
    );
}
