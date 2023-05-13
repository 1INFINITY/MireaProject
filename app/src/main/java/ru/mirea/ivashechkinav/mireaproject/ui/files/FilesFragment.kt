package ru.mirea.ivashechkinav.mireaproject.ui.files

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import kotlinx.coroutines.flow.MutableStateFlow
import ru.mirea.ivashechkinav.mireaproject.databinding.FragmentFilesBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class FilesFragment : Fragment() {

    private lateinit var binding: FragmentFilesBinding
    private var inputTextFlow = MutableStateFlow("")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilesBinding.inflate(inflater, container, false)
        initFields()
        return binding.root
    }
    fun initFields() {
        binding.tvInputFileName.text = INPUT_FILE
        binding.tvOutputFileName.text = OUTPUT_FILE
        binding.btnModify.setOnClickListener {
            val offset = binding.etvOffset.text.toString().toIntOrNull() ?: return@setOnClickListener
            cipherInputInFile(offset)
            binding.tvOutput.text = getTextFromFile(OUTPUT_FILE)
        }
        binding.etvInput.addTextChangedListener {
            setTextToFile(INPUT_FILE, it.toString())
        }
    }
    fun cipherInputInFile(offset: Int) {
        val inputText = getTextFromFile(INPUT_FILE) ?: return
        val outputText = inputText.map{
            (it.code + offset).toChar()
        }.joinToString("")
        setTextToFile(OUTPUT_FILE, outputText)
    }
    fun setTextToFile(fileName: String, text: String) {
        var outputStream: FileOutputStream
        try {
            outputStream = requireActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(text.toByteArray());
            outputStream.close();
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    fun getTextFromFile(fileName: String): String? {
        var fin: FileInputStream? = null
        try {
            fin = requireActivity().openFileInput(fileName)
            val bytes = ByteArray(fin.available())
            fin.read(bytes)
            return String(bytes)
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT).show()
        } finally {
            try {
                if (fin != null) fin.close()
            } catch (ex: IOException) {
                Toast.makeText(requireContext(), ex.message, Toast.LENGTH_SHORT).show()
            }
        }
        return null
    }

    companion object {
        const val INPUT_FILE = "input.txt"
        const val OUTPUT_FILE = "output.txt"
    }
}