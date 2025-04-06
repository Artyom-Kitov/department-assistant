import { useForm, SubmitHandler } from "react-hook-form";
import { createWorkExperienceForEmployee, updateWorkExperienceForEmployee } from "@/api";
import { useEffect, useState } from "react";

interface WorkExperienceFormInputs {
  days: number;
}

interface WorkExperienceFormProps {
  employeeId: string;
  workExperience?: { days: number };
}

export default function WorkExperienceForm({ employeeId, workExperience }: WorkExperienceFormProps) {
  const { register, handleSubmit, setValue } = useForm<WorkExperienceFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (workExperience) {
      setValue("days", workExperience.days || 0);
      setIsExistingData(true);
    }
  }, [workExperience, setValue]);

  const onSubmit: SubmitHandler<WorkExperienceFormInputs> = async (data) => {
    try {
      if (isExistingData) {
        await updateWorkExperienceForEmployee(employeeId, data);
        alert("Опыт работы успешно обновлён");
      } else {
        await createWorkExperienceForEmployee(employeeId, data);
        alert("Опыт работы успешно добавлен");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании опыта работы:", error);
      alert("Ошибка при обновлении/создании опыта работы");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-100 p-6 rounded-lg">
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Опыт работы</h3>

      <label className="text-xs text-gray-500">Количество дней</label>
      <input
        {...register("days", { valueAsNumber: true })}
        type="number"
        min="0"
        placeholder="Количество дней"
        className="w-full p-2 mb-2 border rounded"
      />

      <button
        type="submit"
        className="bg-[#4fff9e] text-gray-700 hover:bg-green-400 px-4 py-2 rounded mt-4 w-full"
      >
        Сохранить
      </button>
    </form>
  );
}
