# OS
Subleq OS 

The subleq instruction ("*SU*btract and *B*ranch if *L*ess than or *EQ*ual to zero") subtracts the contents at address a from the contents at address b, stores the result at address b, and then, if the result is not positive, transfers control to address c (if the result is positive, execution proceeds to the next instruction in sequence).

Pseudocode:
'''
    subleq a, b, c   ; Mem[b] = Mem[b] - Mem[a]
                     ; if (Mem[b] ≤ 0) goto c
'''