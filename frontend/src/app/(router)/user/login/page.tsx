"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import { grey } from "@mui/material/colors";
import { ChangeEvent, useState } from "react";
import { signIn } from "next-auth/react";
import {
    Box,
    Container,
    Grid,
    TextField,
    Typography,
    Button,
    Divider,
} from "@mui/material";

interface HandleLoginProps {
    email: string;
    password: string;
}



export default function App() {
    const router = useRouter();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    // handleValueChange 함수를 컴포넌트 내부에 위치
    function handleValueChange(
        event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
        setFunction: (value: string) => void
    ) {
        setFunction(event.target.value);
    }

    async function handleLogin({ email, password }: HandleLoginProps) {
        const result = await signIn("credentials", {
            redirect: false,
            email,
            password,
        });

        if (result?.error) {
            setError("로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.");
        } else {
            router.push("/");
        }
    }

    function goSocial() {
        window.location.href = `${process.env.NEXT_PUBLIC_SERVER_URL}/oauth2/authorization/kakao?redirect_uri=${process.env.NEXT_PUBLIC_SOCIAL_REDIRECT_URL}&mode=login`;
    }

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                }}
            >
                <Typography component="h1" variant="h5">
                    로그인
                </Typography>
                <Box
                    component="form"
                    onSubmit={(event) => {
                        event.preventDefault();
                        handleLogin({ email, password });
                    }}
                    noValidate
                    sx={{ mt: 1 }}
                >
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="이메일"
                        autoComplete="email"
                        value={email}
                        onChange={(event) => handleValueChange(event, setEmail)}
                        autoFocus
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="password"
                        label="비밀번호"
                        type="password"
                        value={password}
                        onChange={(event) => handleValueChange(event, setPassword)}
                        autoComplete="current-password"
                    />
                    {error && (
                        <Typography color="error" variant="body2">
                            {error}
                        </Typography>
                    )}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{
                            mt: 3,
                            mb: 2,
                            height: "56px",
                        }}
                    >
                        로그인
                    </Button>
                    <Grid
                        container
                        sx={{
                            display: "flex",
                            justifyContent: "space-between",
                        }}
                    ></Grid>
                    <Divider
                        sx={{
                            mt: 2,
                            color: grey[500],
                        }}
                    >
                        또는
                    </Divider>
                    <Grid container spacing={1} mt={2}>
                        <Grid item xs={12}>
                            <Box
                                sx={{
                                    width: "100%",
                                    height: "56px",
                                    cursor: "pointer",
                                    position: "relative",
                                }}
                                onClick={() => goSocial()}
                            >
                                <Image
                                    src="/kakao_login_medium_narrow.png"
                                    alt="kakaoLogin"
                                    layout="fill"
                                    objectFit="contain"
                                />
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            </Box>
        </Container>
    );
}
