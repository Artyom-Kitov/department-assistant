export const processSchema = {
  id: "1",
  name: "Трудоустройство",
  duration: "",
  steps: [
    {
      id: "1",
      type: "start",
      next: "2",
    },

    {
      id: "2",
      type: "predicate",
      description: "Судим?",
      true: {
        goToStep: "3",
      },
      false: {
        goToStep: "4",
      },
    },

    {
      id: "3",
      type: "predicate",
      description: "Есть справка?",
      true: {
        goToStep: "5",
      },
      false: {
        negativeEnd: "3", // передаем id шага, где прервалось
      },
    },

    {
      id: "4",
      type: "predicate",
      description: "Без опыта?",
      true: {
        negativeEnd: "4", // передаем id шага, где прервалось
      },
      false: {
        goToStep: "6",
      },
    },

    {
      id: "5",
      type: "checkblock",
      description: "Задания",
      checks: [
        {
          id: "1",
          description: "Занес справку",
          checked: "boolean",
        },
        {
          id: "2",
          description: "Отжался 10 раз",
          checked: "boolean",
        },
        {
          id: "3",
          description: "Подтянулся",
          checked: "boolean",
        },
      ],
      allChecked: {
        goToProcess: "2",
      },
    },

    {
      id: "6",
      type: "block",
      description: "Посмотреть ютубчик",
      next: "positiveEnd",
    },
  ],
};
