import { useForm, SubmitHandler } from "react-hook-form";
import {
  createEmploymentStatusForEmployee,
  updateEmploymentStatusForEmployee,
} from "@/api";
import { useEffect, useState } from "react";

interface EmploymentStatusFormInputs {
  isEmployedInNsu: boolean;
  employmentInfo: string;
}

interface EmploymentStatusFormProps {
  employeeId: string;
  employmentStatus?: { isEmployedInNsu: boolean; employmentInfo: string };
}

export default function EmploymentStatusForm({
  employeeId,
  employmentStatus,
}: EmploymentStatusFormProps) {
  const { register, handleSubmit, setValue } =
    useForm<EmploymentStatusFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (employmentStatus) {
      setValue("isEmployedInNsu", employmentStatus.isEmployedInNsu);
      setValue("employmentInfo", employmentStatus.employmentInfo || "");
      setIsExistingData(true);
    }
  }, [employmentStatus, setValue]);

  const onSubmit: SubmitHandler<EmploymentStatusFormInputs> = async (data) => {
    try {
      if (isExistingData) {
        await updateEmploymentStatusForEmployee(employeeId, data);
        alert("Статус занятости успешно обновлён");
      } else {
        await createEmploymentStatusForEmployee(employeeId, data);
        alert("Статус занятости успешно добавлен");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании статуса занятости:", error);
      alert("Ошибка при обновлении/создании статуса занятости");
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="bg-gray-100 p-6 rounded-lg flex flex-col"
    >
      <h3 className="text-lg font-semibold text-gray-600 mb-4">
        Статус занятости
      </h3>
      <div className="flex items-center mb-2">
        <input
          {...register("isEmployedInNsu")}
          type="checkbox"
        />
        <label className="text-sm ml-2 text-gray-500">Работает в НГУ?</label>
      </div>

      <label className="text-xs text-gray-500">Дополнительная информация</label>
      <textarea
        {...register("employmentInfo")}
        placeholder="Информация о занятости"
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
