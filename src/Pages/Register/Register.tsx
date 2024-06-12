import { zodResolver } from '@hookform/resolvers/zod';
import { FirebaseError } from 'firebase/app';
import { createUserWithEmailAndPassword } from 'firebase/auth';
import { useEffect, useState } from 'react';
import { FieldValues, useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import Swal from 'sweetalert2';
import { z } from 'zod';

import { auth } from '../../Firebase/FirebaseSDK';
import { FirebaseErrorCodes } from '../../types/FirebaseAuthTypes';
import './Register.css';

const formSchema = z
  .object({
    email: z
      .string()
      .email('Invalid email format')
      .max(40, 'The maximum length of 40 characters has been exceeded'),
    password: z
      .string()
      .min(8, 'The password must contain at least 8 characters')
      .max(40, 'The maximum length of 40 characters has been exceeded')
      .regex(
        /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]+$/,
        'The password must contain at least one uppercase letter, one lowercase letter, one number, and one special character',
      ),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'The passwords do not match',
    path: ['confirmPassword'],
  });

function Register() {
  const [firebaseStatus, setFirebaseStatus] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    reset,
    setFocus,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(formSchema),
    reValidateMode: 'onSubmit',
  });

  useEffect(() => {
    setFocus('email');
  }, [setFocus]);

  async function handleRegistrationSubmit(data: FieldValues) {
    if (isSubmitting) return;
    setIsSubmitting(true);
    const trimmedEmail = data.email.trim();
    try {
      const userCredential = await createUserWithEmailAndPassword(
        auth,
        trimmedEmail,
        data.password,
      );
      reset();
      if (userCredential.user) {
        navigate('/');
        const Toast = Swal.mixin({
          toast: true,
          position: 'top-end',
          showConfirmButton: false,
          timer: 3000,
          timerProgressBar: true,
          didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
          },
        });
        Toast.fire({
          icon: 'success',
          title: 'Registered successfully',
        });
      } else {
        setFirebaseStatus('User not found. Please try again.');
      }
    } catch (error) {
      const err = error as FirebaseError;
      if (err.code) {
        console.error(err.message);
        setFirebaseStatus(
          err.code === FirebaseErrorCodes.AlreadyInUse
            ? 'The email is already in use. Try another one.'
            : 'An unexpected error occurred. Please try again.',
        );
      } else {
        console.error(err);
        setFirebaseStatus('An unexpected error occurred. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <div className="register-container">
      <form
        onSubmit={handleSubmit(handleRegistrationSubmit)}
        noValidate
        className="register-form"
      >
        <h1 className="form-title">Sign Up</h1>
        <input
          autoComplete="email"
          placeholder="e-mail"
          type="email"
          {...register('email')}
          className="form-input"
        />
        {errors.email && (
          <p className="error-text">{errors.email.message?.toString()}</p>
        )}
        <input
          autoComplete="current-password"
          placeholder="password"
          type="password"
          {...register('password')}
          className="form-input"
        />
        {errors.password && (
          <p className="error-text">{errors.password.message?.toString()}</p>
        )}
        <input
          autoComplete="new-password"
          placeholder="confirm password"
          type="password"
          {...register('confirmPassword')}
          className="form-input"
        />
        {errors.confirmPassword && (
          <p className="error-text">
            {errors.confirmPassword.message?.toString()}
          </p>
        )}
        <p className="password-requirements">
          The password must contain at least 8 characters, one uppercase letter,
          one lowercase letter, one number, and one special character.
        </p>
        <button
          onClick={() => setFirebaseStatus(null)}
          type="submit"
          disabled={isSubmitting}
          className="submit-button"
        >
          Sign Up
        </button>
        <h3 className="error-text">{firebaseStatus}</h3>
      </form>
    </div>
  );
}

export default Register;
