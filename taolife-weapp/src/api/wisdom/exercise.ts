import { get } from '@/utils/request';
import type { ExerciseQueryParam, ExercisePageResult, ExerciseDetail } from '@/types/biz/wisdom/exercise';

export const getExercisePage = async (param: ExerciseQueryParam) => {
  return get<ExercisePageResult>('/api/wisdom/exercise/getExercisePage', param);
};

export const getExerciseDetail = async (id: string) => {
  return get<ExerciseDetail>('/api/wisdom/exercise/getExerciseDetail', { id });
};
