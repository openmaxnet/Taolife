export interface FilterOption {
  label: string;
  value: string | number | null;
}

export interface FilterDimension {
  /** 对应后端参数名 */
  key: string;
  options: FilterOption[];
  defaultValue?: string | number | null;
}

export type FilterParams = Record<string, string | number | null>;
