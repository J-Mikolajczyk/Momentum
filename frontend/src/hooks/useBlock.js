import { useState, useEffect, useCallback } from 'react';
import { postRequest, getRequest } from '../utils/api';
import Week from '../models/Week';
import Exercise from '../models/Exercise';
import Set from '../models/Set';

const ip = import.meta.env.VITE_IP_ADDRESS;

export default function useBlock(blockName, userInfo) {
  const userId = userInfo?.id ?? null;

  const [blockData, setBlockData] = useState(null);
  const [currentWeekNum, setCurrentWeekNum] = useState(1);
  const [currentDayIndex, setCurrentDayIndex] = useState(0);
  const [weekText, setWeekText] = useState('Loading...');
  const [message, setMessage] = useState(null);
  const [ignoreMethod, setIgnoreMethod] = useState(null);

  const refreshBlockInternal = useCallback(async (newWeekNumToSet, newDayNumToSet) => {
    if (!userId || !blockName) {
      setBlockData(null);
      setCurrentWeekNum(1);
      setCurrentDayIndex(0);
      setWeekText(userId && blockName ? 'Loading...' : 'Please select a block or user.');
      return;
    }
    try {
      const refreshResponse = await getRequest(`${ip}/secure/block/get`, { blockName, userId });
      if (refreshResponse.ok) {
        const json = await refreshResponse.json();
        if (json.exists === false) {
          setBlockData(null);
          setWeekText('Block not found.');
          setCurrentWeekNum(1);
          setCurrentDayIndex(0);
          return;
        }
        
        setBlockData(json);

        const targetWeekNum = Math.min(newWeekNumToSet, json.weeks?.length || 1);
        const weekForDayCount = json.weeks?.[targetWeekNum - 1];
        const maxDayIndexForTargetWeek = Math.max(0, (weekForDayCount?.days?.length || 1) - 1);
        const targetDayIndex = Math.min(newDayNumToSet - 1, maxDayIndexForTargetWeek);
        
        const day = json.weeks?.[targetWeekNum - 1]?.days?.[targetDayIndex];
        const dayName = day?.name || `Day ${targetDayIndex + 1}`;

        setCurrentWeekNum(targetWeekNum);
        setCurrentDayIndex(targetDayIndex);
        setWeekText(`Week ${targetWeekNum} Day ${targetDayIndex + 1} ${dayName}`);
      } else {
        console.log('Non-OK response during refresh');
        setWeekText('Error loading block.');
      }
    } catch (err) {
      console.log(err);
      setWeekText('Error loading block.');
    }
  }, [blockName, userId]);

  useEffect(() => {
    if (blockName && userId) {
      refreshBlockInternal(1, 1); 
    } else {
      setBlockData(null);
      setWeekText('Loading user or block info...');
      setCurrentWeekNum(1);
      setCurrentDayIndex(0);
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [blockName, userId, refreshBlockInternal]);

  const updateBlock = useCallback(async (updatedWeeks, newWeekNumAfterUpdate = currentWeekNum, newDayNumAfterUpdate = currentDayIndex + 1) => {
    if (!blockData?.id || !blockData?.name) {
      console.log("Block data, ID, or name is missing, cannot update.");
      return;
    }
    const { id, name } = blockData;
    try {
      const updateResponse = await postRequest(`${ip}/secure/block/update`, { name, id, weeks: updatedWeeks });
      if (updateResponse.ok) {
        await refreshBlockInternal(newWeekNumAfterUpdate, newDayNumAfterUpdate);
      } else {
        console.log('Issue updating block');
        setMessage('Failed to update block. Please try again.');
      }
    } catch (err) {
      console.log(err);
      setMessage('An error occurred while updating. Please try again.');
    }
  }, [blockData, refreshBlockInternal, currentWeekNum, currentDayIndex]);

  const setDisplayWeekAndDay = useCallback((weekNum, dayNum) => {
    refreshBlockInternal(weekNum, dayNum);
  }, [refreshBlockInternal]);

  const proceedToAddWeek = useCallback(async () => {
    if (!blockData) return;
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks)); // Deep copy
    if (newWeeks.length === 0 || newWeeks[newWeeks.length - 1].days.length === 0) {
      newWeeks.push(new Week());
    } else {
      newWeeks.push(new Week(newWeeks[newWeeks.length - 1].days));
    }
    await updateBlock(newWeeks, newWeeks.length, 1);
  }, [blockData, updateBlock]);

  const addWeek = useCallback(async () => {
    if (!blockData) return;
    if (blockData.weeks.length >= 6) {
      setMessage("Mesocycles longer than 6 weeks are not recommended. Please consider a deload.");
      setIgnoreMethod(() => () => proceedToAddWeek());
      return;
    }
    setIgnoreMethod(null);
    await proceedToAddWeek();
  }, [blockData, proceedToAddWeek]);

  const proceedToRemoveWeek = useCallback(async () => {
    if (!blockData || blockData.weeks.length <= 1) return;
    const newWeeks = JSON.parse(JSON.stringify(blockData.weeks)); // Deep copy
    newWeeks.pop();
    const newSelectedWeekNum = Math.min(currentWeekNum, newWeeks.length);
    await updateBlock(newWeeks, newSelectedWeekNum, currentDayIndex + 1);
  }, [blockData, updateBlock, currentWeekNum, currentDayIndex]);

  const removeWeek = useCallback(async () => {
    if (!blockData) return;
    if (blockData.weeks.length === 1) {
      setMessage("Cannot remove only week in training block.");
      setIgnoreMethod(() => null);
      return;
    } else if (blockData.weeks.length <= 4) {
      setMessage("Mesocycles less than 4 weeks are not recommended.");
      setIgnoreMethod(() => () => proceedToRemoveWeek());
      return;
    }
    setIgnoreMethod(null);
    await proceedToRemoveWeek();
  }, [blockData, proceedToRemoveWeek]);

  const addExerciseToDay = useCallback(async (exerciseName, targetWeekNumIndex, targetDayIndex) => {
    if (!blockData || !exerciseName) return;
    const newBlockDataWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    for (let i = targetWeekNumIndex; i < newBlockDataWeeks.length; i++) {
      if (newBlockDataWeeks[i] && newBlockDataWeeks[i].days[targetDayIndex]) {
        if (!newBlockDataWeeks[i].days[targetDayIndex].exercises) {
          newBlockDataWeeks[i].days[targetDayIndex].exercises = [];
        }
        newBlockDataWeeks[i].days[targetDayIndex].exercises.push(new Exercise(exerciseName));
      }
    }
    await updateBlock(newBlockDataWeeks, targetWeekNumIndex + 1, targetDayIndex + 1);
  }, [blockData, updateBlock]);

  const addSetToExercise = useCallback(async (exerciseNameToAddSetTo, targetWeekNumIndex, targetDayIndex) => {
    if (!blockData) return;
    const newBlockDataWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    for (let i = targetWeekNumIndex; i < newBlockDataWeeks.length; i++) {
      const day = newBlockDataWeeks[i]?.days?.[targetDayIndex];
      if (day) {
        const exercise = day.exercises?.find(ex => ex.name === exerciseNameToAddSetTo);
        if (exercise) {
          if (!exercise.sets) exercise.sets = [];
          exercise.sets.push(new Set());
        }
      }
    }
    await updateBlock(newBlockDataWeeks, targetWeekNumIndex + 1, targetDayIndex + 1);
  }, [blockData, updateBlock]);

  const deleteSetFromExercise = useCallback(async (exerciseNameToDeleteFrom, setIndexToDelete, targetWeekNumIndex, targetDayIndex) => {
  if (!blockData) return;

  const newBlockDataWeeks = JSON.parse(JSON.stringify(blockData.weeks));

  const syncSetsAcrossWeeks = (startingWeekIndex) => {
    const targetExercise = newBlockDataWeeks[startingWeekIndex]?.days?.[targetDayIndex]?.exercises?.find(ex => ex.name === exerciseNameToDeleteFrom);

    if (targetExercise) {
      const numSets = targetExercise.sets.length;

      for (let i = startingWeekIndex + 1; i < newBlockDataWeeks.length; i++) {
        const futureDay = newBlockDataWeeks[i]?.days?.[targetDayIndex];
        if (futureDay) {
          const futureExerciseIndex = futureDay.exercises?.findIndex(ex => ex.name === exerciseNameToDeleteFrom);
          if (futureExerciseIndex !== -1 && futureDay.exercises[futureExerciseIndex]) {
            const futureExercise = futureDay.exercises[futureExerciseIndex];
            const diff = futureExercise.sets.length - numSets;

            if (diff > 0) {
              futureExercise.sets.splice(numSets);
            } else if (diff < 0) {
              for (let i = 0; i < Math.abs(diff); i++) {
                futureExercise.sets.push({});
              }
            }
          }
        }
      }
    }
  };
  for (let i = targetWeekNumIndex; i < newBlockDataWeeks.length; i++) {
    const day = newBlockDataWeeks[i]?.days?.[targetDayIndex];
    if (day) {
      const exerciseIndex = day.exercises?.findIndex(ex => ex.name === exerciseNameToDeleteFrom);
      if (exerciseIndex !== -1 && day.exercises[exerciseIndex]) {
        const exercise = day.exercises[exerciseIndex];
        if (exercise.sets && exercise.sets.length > setIndexToDelete) {
          exercise.sets.splice(setIndexToDelete, 1);

          if (exercise.sets.length === 0) {
            day.exercises.splice(exerciseIndex, 1);
          }

          syncSetsAcrossWeeks(i);
          break; 
        }
      }
    }
  }
  await updateBlock(newBlockDataWeeks, targetWeekNumIndex + 1, targetDayIndex + 1);
}, [blockData, updateBlock]);


  const updateSetData = useCallback(async (exerciseNameToUpdate, setIndex, field, value, targetWeekNumIndex, targetDayIndex) => {
    if (!blockData) return;
    const newBlockDataWeeks = JSON.parse(JSON.stringify(blockData.weeks));
    const day = newBlockDataWeeks[targetWeekNumIndex]?.days?.[targetDayIndex];
    if (day) {
      const exercise = day.exercises?.find(ex => ex.name === exerciseNameToUpdate);
      if (exercise && exercise.sets && exercise.sets[setIndex]) {
        exercise.sets[setIndex][field] = field === 'weight' || field === 'reps' ? Number(value) || 0 : value;
      }
    }

    await updateBlock(newBlockDataWeeks, currentWeekNum, currentDayIndex + 1);
  }, [blockData, updateBlock, currentWeekNum, currentDayIndex]);
  
  const refreshBlock = useCallback(() => {
      refreshBlockInternal(currentWeekNum, currentDayIndex + 1);
  }, [refreshBlockInternal, currentWeekNum, currentDayIndex]);


  return {
    blockData,
    currentWeekNum,
    currentDayIndex,
    weekText,
    message,
    setMessage,
    ignoreMethod,
    setIgnoreMethod,
    setDisplayWeekAndDay,
    addWeek,
    removeWeek,
    refreshBlock,
    addExerciseToDay,
    addSetToExercise,
    deleteSetFromExercise,
    updateSetData,
  };
}
