import React, { useState } from 'react';
import ExerciseCard from './ExerciseCard';
import AddExercisePopup from './AddExercisePopup';
import MessagePopup from './MessagePopup';
import { useBlockDataContext } from '../contexts/BlockDataContext';

export default function Day({ }) {

    const {
    blockData,
    currentWeekIndex,
    currentDayIndex,
    addExerciseToDay,
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData,
    moveExercise,
    renameExercise,
    deleteExercise,
    updateWeeks
  } = useBlockDataContext();

    const logDay = () => {
      const day = blockData.weeks[currentWeekIndex]?.days?.[currentDayIndex];
      if (!day) return;

      for (const exercise of day.exercises || []) {
        for (const set of exercise.sets || []) {
          if (!set.logged) {
            setMessage('Cannot log day until all sets are logged.');
            return;
          }
        }
      }

      setMessage('Logging a day is irreversible. Are you sure?');
      setIgnoreMethod(() => () => handleLog());
    };


    const handleLog = async () => {
      const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
      const day = newWeeks[currentWeekIndex]?.days?.[currentDayIndex];
      if (!day) return;

      day.logged = true;
      newWeeks[currentWeekIndex].days[currentDayIndex] = day;
      updateWeeks(newWeeks);
    };


  if (!blockData) {
    return;
  }

  const [showAddExercisePopup, setShowAddExercisePopup] = useState(false);
  const [message, setMessage] = useState('');
  const [ignoreMethod, setIgnoreMethod] = useState(null);

  const toggleAddExercisePopup = () => {
    setShowAddExercisePopup(!showAddExercisePopup);
  };

  const currentWeek = blockData?.weeks?.[currentWeekIndex];
  const currentDay = currentWeek?.days?.[currentDayIndex];
  const exercises = currentDay?.exercises;

  const previousWeek = currentWeekIndex > 0 ? blockData?.weeks?.[currentWeekIndex - 1]: null;
  const previousDay = previousWeek?.days?.[currentDayIndex];
  const previousExercises = previousDay?.exercises || [];


  return (
    <div className="flex flex-col w-full flex-grow items-start gap-4 px-4 pt-4 overflow-y-auto scrollbar-hide min-h-[100dvh] pb-50" >
      {exercises?.length > 0 ? (
        exercises.map((exercise, exerciseIndex) => {
          const priorExercise = previousExercises.find(
            (prevExercise) => prevExercise.name === exercise.name
          );
          return (
              <ExerciseCard
                key={exerciseIndex}
                exercise={exercise}
                priorExercise={priorExercise}
                exerciseIndex={exerciseIndex}
                currentWeekIndex={currentWeekIndex}
                currentDayIndex={currentDayIndex}
                addSetToExercise={addSetToExercise}
                deleteSetFromExercise={deleteSetFromExercise}
                updateSetData={updateSetData}
                moveExercise={moveExercise}
                renameExercise={renameExercise}
                deleteExercise={deleteExercise}
                dayLogged={currentDay.logged}
              />
          );
        })
      ) : null}
      {currentDay.logged ? null : 
      <div className="flex flex-row w-full items-center justify-between">
        <button onClick={() => logDay()} className="flex bg-gray-400 text-gray-500 font-anton w-1/4 min-w-28 h-8 text-lg border items-center justify-center border-gray-500 rounded-xs cursor-pointer">Log Day</button>
        <button onClick={toggleAddExercisePopup} className="flex elect-none bg-gray-400 text-gray-500 font-anton w-1/4 min-w-28 h-8 text-lg border items-center justify-center border-gray-500 rounded-xs cursor-pointer ">Add Exercise</button>
        </div>}
        <AddExercisePopup
          show={showAddExercisePopup}
          toggle={toggleAddExercisePopup}
          currentDayIndex={currentDayIndex}
          currentWeekIndex={currentWeekIndex}
          addExerciseToDay={addExerciseToDay}
        />
        <MessagePopup
          message={message}
          setMessage={setMessage}
          ignoreMethod={ignoreMethod}
          setIgnoreMethod={setIgnoreMethod}
          buttonText="Continue"
        />
    </div>
  );
}
