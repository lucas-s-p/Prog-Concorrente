import sys
import threading

sum_total = 0
mutex = threading.Semaphore(1)

sums = []

def do_sum(path, multiplex):
    multiplex.acquire()
    try:
        global sums
        global sum_total
        _sum = 0
        with open(path, 'rb') as f:
            byte = f.read(1)
            while byte:
                _sum += int.from_bytes(byte, byteorder='big', signed=False)
                byte = f.read(1)
        sums.append((path, _sum))
        mutex.acquire()
        try:
            sum_total += _sum
        finally:
            mutex.release()
    finally:
        multiplex.release()

#many error could be raised error. we don't care       
if __name__ == "__main__":
    paths = sys.argv[1:]
    num_threads = len(paths) // 2
    multiplex = threading.Semaphore(num_threads)
    threads = []
    for path in paths:
        try:
            thread = threading.Thread(target=do_sum, args=(path,multiplex,))
            threads.append(thread)
            thread.start()
        except Exception as e:
            print(f"Erro ao processar {path}: {e}")

    for thread in threads:
        thread.join()

    print("A soma total é: " + str(sum_total))
    
    valores_unicos = set(valor[1] for valor in sums)
    for valor in valores_unicos:
        arquivos_valor_igual = [s[0] for s in sums if s[1] == valor]
        if len(arquivos_valor_igual) > 1:
            print(f"Arquivos com valores iguais a {valor} são: {', '.join(arquivos_valor_igual)}")
