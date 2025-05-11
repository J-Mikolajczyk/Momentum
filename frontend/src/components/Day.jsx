import React, { useState } from 'react';
import ExerciseCard from './ExerciseCard';
import AddExercisePopup from './AddExercisePopup';

export default function Day({
  blockData,
  currentWeekIndex,
  currentDayIndex,
  addExerciseToDay,
  addSetToExercise,
  deleteSetFromExercise,
  updateSetData,
}) {
  const [showAddExercisePopup, setShowAddExercisePopup] = useState(false);

  const toggleAddExercisePopup = () => {
    setShowAddExercisePopup(!showAddExercisePopup);
  };

  const currentWeek = blockData?.weeks?.[currentWeekIndex];
  const currentDay = currentWeek?.days?.[currentDayIndex];
  const exercises = currentDay?.exercises;

  return (
    <div className="flex flex-col w-full flex-grow items-center gap-2 pb-8">
      {exercises?.length > 0 ? (
        exercises.map((exercise, exerciseIndex) => (
          <ExerciseCard
            key={exerciseIndex}
            exercise={exercise}
            currentWeekIndex={currentWeekIndex}
            currentDayIndex={currentDayIndex}
            addSetToExercise={addSetToExercise}
            deleteSetFromExercise={deleteSetFromExercise}
            updateSetData={updateSetData}
          />
        ))
      ) : null}
      <div className="flex flex-row w-full items-center justify-between">
        <button
          onClick={toggleAddExercisePopup}
          className="flex elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-28 h-8 text-lg border items-center justify-center border-gray-500 rounded-xs ml-auto"
        >
          Add Exercise
        </button>
        <AddExercisePopup
          show={showAddExercisePopup}
          toggle={toggleAddExercisePopup}
          currentDayIndex={currentDayIndex}
          currentWeekIndex={currentWeekIndex}
          addExerciseToDay={addExerciseToDay}
        />
      </div>
    </div>
  );
}
