import matplotlib.pyplot as plt

# Dati per i grafici
eq =     [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
r =     [0, 0, 0.001, 0.000, 0.000, 0.002, 0.006, 0.019, 0.076, 0.613, 1.795, 6.246, 30.159, 91.297]
ef =    [0, 0, 0.001, 0.001, 0.001, 0.006, 0.013, 0.030, 0.096, 0.316, 1.501, 6.143, 28.065, 89.49]
dif = [r[i] - ef[i] for i in range(len(r))]


plt.figure(figsize=(8, 6))

plt.plot(eq, r, label='No Heuristics', color='blue')

plt.plot(eq, ef, label='All Heuristics', color='red')

plt.plot(eq, dif, label='Difference', color='green')

plt.yscale('symlog', linthresh=0.1)

plt.xlabel('eq_diamond_i')
plt.ylabel('seconds')

plt.xticks([2, 4, 6, 8, 9, 10, 12, 14])

plt.legend()

plt.grid(True)
plt.show()

print("ok")