import { useForm, SubmitHandler } from "react-hook-form";
import { createPassportInfoForEmployee, updatePassportInfoForEmployee } from "@/api";
import { useEffect, useState } from "react";

interface PassportFormInputs {
  passportInfo: string;
}

interface PassportFormProps {
  employeeId: string;
  passportInfo?: { passportInfo: string };
}

export default function PassportForm({ employeeId, passportInfo }: PassportFormProps) {
  const { register, handleSubmit, setValue } = useForm<PassportFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (passportInfo) {
      setValue("passportInfo", passportInfo.passportInfo || "");
      setIsExistingData(true);
    }
  }, [passportInfo, setValue]);

  const onSubmit: SubmitHandler<PassportFormInputs> = async (data) => {
    try {
      if (isExistingData) {
        console.log("Updating passportInfo...");
        await updatePassportInfoForEmployee(employeeId, data);
        alert("Паспортная информация успешно обновлена");
      } else {
        console.log("Creating passportInfo...");
        await createPassportInfoForEmployee(employeeId, data);
        alert("Паспортная информация успешно добавлена");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании паспортной информации:", error);
      alert("Ошибка при обновлении/создании паспортной информации");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-100 p-6 rounded-lg">
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Паспортные данные</h3>

      <label className="text-xs text-gray-500">Паспортные данные</label>
      <textarea
        {...register("passportInfo")}
        placeholder="Введите паспортную информацию"
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
