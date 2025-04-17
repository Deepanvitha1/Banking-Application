"use client";
import React, { useState } from "react";
import Image from "next/image";
import { useRouter } from "next/navigation";
import axios from "axios";
import { setCookie } from "cookies-next";
import loginlogo from "@/assets/hugohub_logo.jpeg";
import "@/components/login-component/Login.css";
import { regex } from "@/components/regular-expressions/regex";

const Login = () => {
  const [formData, setFormData] = useState({ phoneNumber: "", password: "" });
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const router = useRouter();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const validateForm = () => {
    if (!regex.phoneRegex.test(formData.phoneNumber)) {
      return "Phone number must be exactly 10 digits.";
    }
    if (formData.password.trim() === "") {
      return "Password cannot be empty.";
    }
    return "";
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    const validationError = validateForm();
    if (validationError) {
      setErrorMessage(validationError);
      return;
    }

    try {
      const res = await axios.post("/api/login", formData);
      console.log(res);
      if (
        res.data.data.headers.status_code === "200" &&
        res.data.data.data.jwtToken
      ) {
        setCookie("token", res.data.data.data.jwtToken);
        setCookie("userId", res.data.data.data.uid);
        router.push("/dashboard");
      } else {
        setErrorMessage("Login failed. Please try again.");
      }
    } catch (error) {
      if (axios.isAxiosError(error) && error.response) {
        if (error.response.status === 400) {
          setErrorMessage("Invalid credentials. Please try again.");
        } else if (error.response.status === 403) {
          setErrorMessage("Access Denied. Please check your login details.");
        } else {
          setErrorMessage("Something went wrong. Please try again.");
        }
      } else {
        setErrorMessage(
          "Unable to connect to the server. Please try again later."
        );
      }
    }
  };

  return (
    <div className="loginContainer">
      <div className="loginImgContainer">
        <Image
          className="loginImg"
          src={loginlogo}
          alt="logo"
          width={200}
          height={200}
        />
      </div>
      <div className="loginDetails">
        <h2>Log In</h2>
        {errorMessage && <div className="error">{errorMessage}</div>}
        <form className="loginForm" onSubmit={handleSubmit}>
          <div>
            <input
              type="number"
              name="phoneNumber"
              placeholder="Phone Number"
              value={formData.phoneNumber}
              onChange={handleChange}
              className="inputField"
            />
          </div>
          <div>
            <input
              type="password"
              name="password"
              placeholder="Password"
              value={formData.password}
              onChange={handleChange}
              className="inputField"
            />
          </div>
          <button type="submit" className="logIn1">
            Log In
          </button>
        </form>
        <div className="signUp-loginLink">
          <p>
            Do not have an account?{" "}
            <span
              onClick={() => router.push("/signup")}
              className="signUp-login"
            >
              Sign Up
            </span>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
