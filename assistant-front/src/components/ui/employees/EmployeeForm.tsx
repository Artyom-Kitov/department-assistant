import { useForm, SubmitHandler } from "react-hook-form";
import { Employee, updateEmployee } from "@/api";
import { useEffect } from "react";

interface EmployeeFormInputs {
  id: string;
  firstName: string;
  lastName: string;
  middleName: string;
  agreement: boolean;
  hasCompletedAdvancedCourses: boolean;
  hasHigherEducation: boolean;
  needsMandatoryElection: boolean;
  snils: string | null;
  inn: string | null;
  isArchived: boolean;
}

interface EmployeeFormProps {
  employee: Employee;
}

export default function EmployeeForm({ employee }: EmployeeFormProps) {
  const { register, handleSubmit, setValue } = useForm<EmployeeFormInputs>();

  useEffect(() => {
    if (employee) {
      setValue("id", employee.id);
      setValue("firstName", employee.firstName || "");
      setValue("lastName", employee.lastName || "");
      setValue("middleName", employee.middleName || "");
      setValue("agreement", employee.agreement || false);
      setValue(
        "hasCompletedAdvancedCourses",
        employee.hasCompletedAdvancedCourses || false
      );
      setValue("hasHigherEducation", employee.hasHigherEducation || false);
      setValue(
        "needsMandatoryElection",
        employee.needsMandatoryElection || false
      );
      setValue("snils", employee.snils || null);
      setValue("inn", employee.inn || null);
      setValue("isArchived", employee.isArchived || false);
    }
  }, [employee, setValue]);

  const onSubmit: SubmitHandler<EmployeeFormInputs> = async (data) => {
    if (!data.id) {
      alert("Ошибка: ID сотрудника не найден.");
      return;
    }

    if (!data.firstName || !data.lastName) {
      alert("Пожалуйста, заполните все обязательные поля.");
      return;
    }

    // Устанавливаем null, если поля пустые
    const sanitizedData = {
      ...data,
      snils: data.snils?.trim() === "" ? null : data.snils,
      inn: data.inn?.trim() === "" ? null : data.inn,
    };

    try {
      console.log(sanitizedData);
      await updateEmployee(data.id, sanitizedData);
      alert("Сотрудник успешно обновлён");
    } catch (error) {
      console.error("Ошибка при обновлении сотрудника:", error);
      alert("Ошибка при обновлении сотрудника");
    }
  };

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="bg-gray-100 p-6 rounded-lg"
    >
      <h3 className="text-lg font-semibold text-gray-600 mb-4">Сотрудник</h3>

      <label className="text-xs text-gray-500">Имя</label>
      <input
        {...register("firstName")}
        placeholder="Имя"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">Фамилия</label>
      <input
        {...register("lastName")}
        placeholder="Фамилия"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">Отчество</label>
      <input
        {...register("middleName")}
        placeholder="Отчество"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">СНИЛС</label>
      <input
        {...register("snils")}
        placeholder="СНИЛС"
        className="w-full p-2 mb-2 border rounded"
      />

      <label className="text-xs text-gray-500">ИНН</label>
      <input
        {...register("inn")}
        placeholder="ИНН"
        className="w-full p-2 mb-2 border rounded"
      />

      <div className="flex items-center mb-2">
        <input {...register("agreement")} type="checkbox" />
        <label className="ml-2 text-sm text-gray-500">Соглашение</label>
      </div>

      <div className="flex items-center mb-2">
        <input {...register("hasCompletedAdvancedCourses")} type="checkbox" />
        <label className="ml-2 text-sm text-gray-500">
          Курсы повышения квалификации
        </label>
      </div>

      <div className="flex items-center mb-2">
        <input {...register("hasHigherEducation")} type="checkbox" />
        <label className="text-sm ml-2 text-gray-500">Высшее образование</label>
      </div>

      <div className="flex items-center mb-2">
        <input {...register("needsMandatoryElection")} type="checkbox" />
        <label className="text-sm ml-2 text-gray-500">
          Нуждается в обязательных выборах
        </label>
      </div>

      <div className="flex items-center">
        <input {...register("isArchived")} type="checkbox" />
        <label className="text-sm ml-2 text-gray-500">Архивировано</label>
      </div>

      <button
        type="submit"
        className="bg-[#4fff9e] text-gray-700 hover:bg-green-400 px-4 py-2 rounded mt-4 w-full"
      >
        Сохранить
      </button>
    </form>
  );
}
