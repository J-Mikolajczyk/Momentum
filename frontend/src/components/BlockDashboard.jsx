import React, { useEffect, useState } from 'react';
import { postRequest, getRequest } from '../utils/api';
import Day from './Day';
import WeekMenu from './WeekMenu';
import Exercise from '../models/Exercise';
import { useBlockDashboardContext } from '../contexts/BlockDashboardContext';
import { BlockDataContext } from '../contexts/BlockDataContext';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function BlockDashboard({ }) {

  const {
    blockName,
    userInfo,
    setWeekText,
    weekText,
    logOut
  } = useBlockDashboardContext();

  const [blockData, setBlockData] = useState(null);
  const [currentWeekIndex, setCurrentWeekIndex] = useState(null);
  const [currentDayIndex, setCurrentDayIndex] = useState(null);

  const userId = userInfo?.id;

  useEffect(() => {
    if (blockName) {
      fetchBlock();
    } else {
      setBlockData(null);
    }
  }, [blockName]);

  const computeWeekText = (data, weekNum, dayIndex) => {
    const day = data?.weeks?.[weekNum]?.days?.[dayIndex];
    const dayName = day?.name ?? '';
    return `Week ${weekNum + 1} Day ${dayIndex + 1} ${dayName}`;
  };

  const updateWeeks = async (newWeeks) => {
    updateBlock(newWeeks, currentWeekIndex, currentDayIndex);
  }

  const fetchBlock = async () => {
      if (!userId || !blockName) {
        setBlockData(null);
        setWeekText('Please select a block or user.');
        return;
      }
  
      try {
        const response = await getRequest(`${ip}/secure/block/get`, { blockName, userId });
        if (response.status === 403 || response.status === 401) {
            logOut();
            return;
        }

        if (!response.ok) throw new Error('Failed to update block');
  
        const json = await response.json();
        if (json.exists === false) {
          setBlockData(null);
          setWeekText('Block not found.');
          return;
        }
  
        setBlockData(json);
        const { mostRecentWeekOpen, mostRecentDayOpen } = json;
        setCurrentWeekIndex(mostRecentWeekOpen);
        setCurrentDayIndex(mostRecentDayOpen);
        setWeekText(computeWeekText(json, mostRecentWeekOpen, mostRecentDayOpen));
      } catch (err) {
        console.error(err);
        setWeekText('Error loading block.');
      }
    };

    const updateBlock = async (updatedWeeks, weekIndex, dayIndex, options = {}) => {
      if (!blockData?.id || !blockData?.name) return;

      if (weekIndex === blockData.weeks.length - 1 && updatedWeeks.length < blockData.weeks.length) {
        weekIndex--;
      }

      try {
        const response = await postRequest(`${ip}/secure/block/update`, {
          id: blockData.id,
          name: blockData.name,
          weeks: updatedWeeks,
          weekIndex,
          dayIndex
        });

        if (response.status === 403 || response.status === 401) {
          logOut();
          return;
        }

        if (!response.ok) throw new Error('Failed to update block');

        if (!options.skipRefresh) {
          setBlockData(prev => ({
            ...prev,
            weeks: updatedWeeks,
            mostRecentWeekOpen: weekIndex,
            mostRecentDayOpen: dayIndex
          }));

          setCurrentWeekIndex(weekIndex);
          setWeekText(computeWeekText({ ...blockData, weeks: updatedWeeks }, weekIndex, dayIndex));
          setCurrentDayIndex(dayIndex);
        }
      } catch (err) {
        console.error(err);
        setMessage('Error updating block.');
      }
    };

  const addExerciseToDay = async (exerciseName, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
      for (let i = weekIndex; i < newWeeks.length; i++) {
        const day = newWeeks[i].days?.[dayIndex];
        if (day) {
          day.exercises = day.exercises || [];
          day.exercises.push(new Exercise(exerciseName));
        }
      }
      await updateBlock(newWeeks, currentWeekIndex, currentDayIndex);
  };


  const setWeekAndDay = async (weekNum, dayNum) => {
      setCurrentWeekIndex(weekNum);
      setCurrentDayIndex(dayNum);
      setWeekText(computeWeekText(blockData, weekNum, dayNum));
      await updateBlock(blockData.weeks, weekNum, dayNum, { skipRefresh: true });
    };

  const addSetToExercise = async (exerciseName, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    let baseSetsLength = 0;

    for (let i = weekIndex; i < newWeeks.length; i++) {
      const exercise = newWeeks[i]?.days?.[dayIndex]?.exercises?.find(ex => ex.name === exerciseName);
      if (exercise) {
        exercise.sets = exercise.sets || [];
        if (i === weekIndex) {
          exercise.sets.push({ weight: '', reps: '' });
          baseSetsLength = exercise.sets.length;
        }
      }
    }

    syncAcrossWeeks(baseSetsLength, exerciseName, weekIndex, dayIndex, newWeeks);
    await updateBlock(newWeeks, weekIndex, dayIndex);
  };

  const syncAcrossWeeks = (baseSetsLength, exerciseName, weekIndex, dayIndex, newWeeks) => {
    for (let i = weekIndex + 1; i < newWeeks.length; i++) {
      const day = newWeeks[i]?.days?.[dayIndex];
      if (!day) continue;

      const exercises = day.exercises;
      const exIndex = exercises.findIndex(ex => ex.name === exerciseName);
      if (exIndex === -1) continue;

      const exercise = exercises[exIndex];

      if (baseSetsLength === 0) {
        exercises.splice(exIndex, 1);
      } else {
        const currentLength = exercise.sets.length;
        const diff = currentLength - baseSetsLength;
        if (diff > 0) {
          exercise.sets.splice(baseSetsLength); 
        } else if (diff < 0) {
          for (let j = 0; j < -diff; j++) {
            exercise.sets.push({}); 
          }
        }
      }
    }
  };


  const deleteSetFromExercise = async (exerciseName, setIndex, weekIndex, dayIndex) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));

    const baseWeek = newWeeks[weekIndex];
    const exercises = baseWeek?.days?.[dayIndex]?.exercises;
    const exIndex = exercises?.findIndex(ex => ex.name === exerciseName);
    if (exIndex === -1) return;

    const sets = exercises[exIndex].sets;
    sets.splice(setIndex, 1);

    let baseSetsLength = sets.length;

    if (baseSetsLength === 0) {
      exercises.splice(exIndex, 1);
    }

    syncAcrossWeeks(baseSetsLength, exerciseName, weekIndex, dayIndex, newWeeks);
    await updateBlock(newWeeks, weekIndex, dayIndex);
  };

  const updateSetData = async (exerciseName, setIndex, weight, reps, weekIndex, dayIndex, toggleLog = true) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    const exercise = newWeeks[weekIndex]?.days?.[dayIndex]?.exercises?.find(ex => ex.name === exerciseName);
    if (exercise && exercise.sets[setIndex]) {
      exercise.sets[setIndex].weight = weight;
      exercise.sets[setIndex].reps = reps;
      if (toggleLog) {
        exercise.sets[setIndex].logged = !exercise.sets[setIndex].logged;
      }
    }
    await updateBlock(newWeeks, weekIndex, dayIndex);
  };


  const moveExercise = async (direction, index) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    const day = newWeeks[currentWeekIndex].days[currentDayIndex];
    const exercises = day.exercises;
    if (!exercises || exercises.length < 2) return;
    const targetIndex = direction === 'up' ? index - 1 : index + 1;
    if (targetIndex < 0 || targetIndex >= exercises.length) return;
    [exercises[index], exercises[targetIndex]] = [exercises[targetIndex], exercises[index]];

    const movedExerciseName = exercises[targetIndex].name;
    const swappedWithName = exercises[index].name;

    for (let i = currentWeekIndex + 1; i < newWeeks.length; i++) {
      const futureDay = newWeeks[i]?.days?.[currentDayIndex];
      if (!futureDay) continue;
      const futureExercises = futureDay.exercises;
      const currentIndex = futureExercises.findIndex(ex => ex.name === movedExerciseName);
      const swapIndex = futureExercises.findIndex(ex => ex.name === swappedWithName);
      if (currentIndex === -1 || swapIndex === -1) continue;
      [futureExercises[currentIndex], futureExercises[swapIndex]] = [futureExercises[swapIndex], futureExercises[currentIndex]];
    }

    await updateBlock(newWeeks, currentWeekIndex, currentDayIndex);
  };

  const renameExercise = async (exerciseName, newName) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    const day = newWeeks[currentWeekIndex].days[currentDayIndex];
    const exercises = day.exercises;

    exercises.forEach(ex => {
      if (ex.name === exerciseName) {
        ex.name = newName;
      }
    });

    for (let i = currentWeekIndex + 1; i < newWeeks.length; i++) {
      const futureDay = newWeeks[i]?.days?.[currentDayIndex];
      if (!futureDay) continue;
      futureDay.exercises.forEach(ex => {
        if (ex.name === exerciseName) {
          ex.name = newName;
        }
      });
    }

    await updateBlock(newWeeks, currentWeekIndex, currentDayIndex);
  };


  const deleteExercise = async (exerciseName) => {
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks));

    const day = newWeeks[currentWeekIndex].days[currentDayIndex];
    day.exercises = day.exercises.filter(ex => ex.name !== exerciseName);

    for (let i = currentWeekIndex; i < newWeeks.length; i++) {
      const futureDay = newWeeks[i]?.days?.[currentDayIndex];
      if (!futureDay) continue;
      futureDay.exercises = futureDay.exercises.filter(ex => ex.name !== exerciseName);
    }

    await updateBlock(newWeeks, currentWeekIndex, currentDayIndex);
  };



  return (
    <BlockDataContext.Provider value={{
      blockData,
      currentWeekIndex,
      currentDayIndex,
      weekText,
      updateWeeks,
      setWeekAndDay,
      addExerciseToDay,
      addSetToExercise,
      deleteSetFromExercise,
      updateSetData,
      moveExercise,
      renameExercise,
      deleteExercise
    }}>
      <WeekMenu />
      <Day />
    </BlockDataContext.Provider>
  );
}
