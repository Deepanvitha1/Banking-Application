"use client";

import React, { useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import "@/components/signup-component/Signup.css";
import signuplogo from "@/assets/hugohub_logo.jpeg";
import axios from "axios";
import { regex } from "@/components/regular-expressions/regex";

function SignUp() {
  const Router = useRouter();

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    aadhar: "",
    address: "",
    dob: "",
    password: "",
    rePassword: "",
  });

  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [generalError, setGeneralError] = useState("");

  const validateField = (name: string, value: string) => {
    let error = "";

    switch (name) {
      case "firstName":
      case "lastName":
        error = regex.nameRegex.test(value) ? "" : "Only alphabets are allowed";
        break;

      case "email":
        error = regex.emailRegex.test(value) ? "" : "Invalid email format";
        break;

      case "phoneNumber":
        error = regex.phoneRegex.test(value)
          ? ""
          : "Phone number must be 10 digits";
        break;

      case "aadhar":
        error = regex.aadharRegex.test(value)
          ? ""
          : "Aadhar number must be 12 digits";
        break;

      case "dob":
        const dobDate = new Date(value);
        const today = new Date();
        today.setHours(0, 0, 0, 0); 
        error =
          value && dobDate < today ? "" : "Date of birth must be in the past";
        break;

      case "password":
        error =
          value.length >= 8 && regex.passwordRegex.test(value)
            ? ""
            : "Password must be at least 8 characters and contain letters and numbers";
        break;

      case "rePassword":
        error = value === formData.password ? "" : "Passwords do not match";
        break;

      default:
        break;
    }

    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: error,
    }));
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prevFormData) => ({ ...prevFormData, [name]: value }));
    validateField(name, value);
  };

  const isFormValid = () => {
    return (
      Object.values(errors).every((error) => error === "") &&
      Object.values(formData).every((value) => value.trim() !== "")
    );
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!isFormValid()) {
      return;
    }
    setErrors({});

    try {
      const res = await axios.post("/api/sign-up", formData, {
        headers: { "Content-Type": "application/json" },
      });
      if (res.data.headers.status_code === "200") {
        Router.push("/login");
      } else {
        setErrors((prev) => ({
          ...prev,
          general: res.data.headers.message || "Signup failed",
        }));
      }
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        const errorMessage =
          typeof error.response.data === "string"
            ? error.response.data
            : JSON.stringify(error.response.data);

        if (errorMessage.includes("User with phone number")) {
          setGeneralError("User with this phone number already exists.");
        } else {
          setGeneralError("Something went wrong. Please try again.");
        }
      } else {
        setGeneralError("Something went wrong. Please try again later.");
      }
    }
  };

  return (
    <div className="signUpContainer">
      <div className="leftContainer">
        <div className="signUpImgContainer">
          <Image
            className="signUpImg"
            src={signuplogo}
            alt="Hugo Hub Logo"
            width={600}
            height={600}
          />
        </div>
      </div>
      <div className="rightContainer">
        <div className="signUpDetails">
          <h1 style={{ fontSize: "2em", fontWeight: "200" }}>Sign Up</h1>

          {generalError && (
            <p className="error-message general-error">{generalError}</p>
          )}

          <form className="signupForm" onSubmit={handleSubmit}>
            <input
              type="text"
              placeholder="First Name"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              className="inputField"
            />
            {errors.firstName && (
              <p className="error-message">{errors.firstName}</p>
            )}

            <input
              type="text"
              placeholder="Last Name"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              className="inputField"
            />
            {errors.lastName && (
              <p className="error-message">{errors.lastName}</p>
            )}

            <input
              type="email"
              placeholder="Email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="inputField"
            />
            {errors.email && <p className="error-message">{errors.email}</p>}

            <input
              type="text"
              placeholder="Phone Number"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              className="inputField"
            />
            {errors.phoneNumber && (
              <p className="error-message">{errors.phoneNumber}</p>
            )}

            <input
              type="text"
              placeholder="Aadhar Number"
              name="aadhar"
              value={formData.aadhar}
              onChange={handleChange}
              className="inputField"
            />
            {errors.aadhar && <p className="error-message">{errors.aadhar}</p>}

            <input
              type="text"
              placeholder="Address"
              name="address"
              value={formData.address}
              onChange={handleChange}
              className="inputField"
            />

            <input
              type="date"
              placeholder="Date of Birth"
              name="dob"
              value={formData.dob}
              onChange={handleChange}
              className="inputField"
            />
            {errors.dob && <p className="error-message">{errors.dob}</p>}

            <input
              type="password"
              placeholder="Password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="inputField"
            />
            {errors.password && (
              <p className="error-message">{errors.password}</p>
            )}

            <input
              type="password"
              placeholder="Re-Enter Password"
              name="rePassword"
              value={formData.rePassword}
              onChange={handleChange}
              className="inputField"
            />
            {errors.rePassword && (
              <p className="error-message">{errors.rePassword}</p>
            )}

            <button type="submit" className="signUp1" disabled={!isFormValid()}>
              Sign Up
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default SignUp;
