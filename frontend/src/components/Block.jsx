import React, { useEffect } from 'react';
import Day from './Day';

export default function Block({ currentWeekIndex, currentDayIndex, blockData, addExerciseToDay, addSetToExercise, deleteSetFromExercise, updateSetData }) {

  if (!blockData) {
    return;
  }

  return (
    <>
      <div className="flex flex-row w-full flex-grow items-start gap-4 px-4 pt-4 overflow-y-auto scrollbar-hide" style={{ maxHeight: 'calc(100dvh - 130px)' }}>
        <div className="flex flex-col items-center rd w-full">
          <Day
              blockData={blockData}
              currentWeekIndex={currentWeekIndex}
              currentDayIndex={currentDayIndex}
              addExerciseToDay={addExerciseToDay}
              addSetToExercise={addSetToExercise}
              deleteSetFromExercise={deleteSetFromExercise}
              updateSetData={updateSetData}
            />
        </div>
      </div>
    </>
  );
}
