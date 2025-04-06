import { useForm, SubmitHandler } from "react-hook-form";
import { createAcademicDegreeForEmployee, updateAcademicDegreeForEmployee } from "@/api";
import { useEffect, useState } from "react";

interface AcademicDegreeFormInputs {
  name: string;
}

interface AcademicDegreeFormProps {
  employeeId: string;
  academicDegree?: { name: string };
}

export default function AcademicDegreeForm({ employeeId, academicDegree }: AcademicDegreeFormProps) {
  const { register, handleSubmit, setValue } = useForm<AcademicDegreeFormInputs>();
  const [isExistingData, setIsExistingData] = useState<boolean>(false);

  useEffect(() => {
    if (academicDegree) {
      setValue("name", academicDegree.name || "");
      setIsExistingData(true);
    }
  }, [academicDegree, setValue]);

  const onSubmit: SubmitHandler<AcademicDegreeFormInputs> = async (data) => {
    try {
      if (isExistingData) {
        await updateAcademicDegreeForEmployee(employeeId, data);
        alert("Учёная степень успешно обновлена");
      } else {
        await createAcademicDegreeForEmployee(employeeId, data);
        alert("Учёная степень успешно добавлена");
      }
    } catch (error) {
      console.error("Ошибка при обновлении/создании учёной степени:", error);
      alert("Ошибка при обновлении/создании учёной степени");
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-100 p-6 rounded-lg">
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Учёная степень</h3>

      <label className="text-xs text-gray-500">Название</label>
      <input
        {...register("name")}
        placeholder="Название учёной степени"
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
