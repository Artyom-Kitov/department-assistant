import { useForm, SubmitHandler } from "react-hook-form";
import { createEmploymentRecordForEmployee, updateEmploymentRecordForEmployee } from "@/api";
import { useEffect, useState } from "react";

interface EmploymentRecordFormInputs {
  dateOfReceipt: string;
}

interface EmploymentRecordFormProps {
  employeeId: string;
  employmentRecord?: { dateOfReceipt: string };
}

export default function EmploymentRecordForm({ employeeId, employmentRecord }: EmploymentRecordFormProps) {
  const { register, handleSubmit, setValue } = useForm<EmploymentRecordFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (employmentRecord) {
      setValue("dateOfReceipt", employmentRecord.dateOfReceipt || "");
      setIsExistingData(true);
    }
  }, [employmentRecord, setValue]);

  const onSubmit: SubmitHandler<EmploymentRecordFormInputs> = async (data) => {
    try {
      if (isExistingData) {
        await updateEmploymentRecordForEmployee(employeeId, data);
        alert("Запись о трудоустройстве успешно обновлена");
      } else {
        await createEmploymentRecordForEmployee(employeeId, data);
        alert("Запись о трудоустройстве успешно добавлена");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании записи о трудоустройстве:", error);
      alert("Ошибка при обновлении/создании записи о трудоустройстве");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-100 p-6 rounded-lg">
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Запись о трудоустройстве</h3>

      <label className="text-xs text-gray-500">Дата приёма на работу</label>
      <input
        {...register("dateOfReceipt")}
        type="date"
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
