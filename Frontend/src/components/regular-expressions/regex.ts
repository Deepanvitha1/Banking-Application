export const regex = {
  nameRegex: /^[A-Za-z]+$/,
  emailRegex: /^[a-zA-Z0-9._%+-]+@gmail\.com$/,
  phoneRegex: /^\d{10}$/,
  aadharRegex: /^\d{12}$/,
  dobRegex: /^\d{4}-\d{2}-\d{2}$/,
  passwordRegex:
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
};
