package com.cshisan.feature.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cshisan.core.model.AiModel

/**
 * AI模型选择器
 *
 * @param selectedModel 当前选中的模型
 * @param availableModels 可用的模型列表
 * @param onModelSelected 模型选择回调
 * @param modifier 修改器
 */
@Composable
fun ModelSelector(
  selectedModel: AiModel,
  availableModels: List<AiModel>,
  onModelSelected: (AiModel) -> Unit,
  modifier: Modifier = Modifier
) {
  var expanded by remember { mutableStateOf(false) }
  val rotationState by animateFloatAsState(
    targetValue = if (expanded) 180f else 0f,
    label = "Arrow rotation"
  )

  Column(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
    // 当前选中的模型（下拉框头部）
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.surface)
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
          shape = RoundedCornerShape(8.dp)
        )
        .clickable { expanded = !expanded }
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "AI助手",
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.weight(1f))

      Text(
        text = selectedModel.name,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary
      )

      Spacer(modifier = Modifier.width(4.dp))

      Icon(
        imageVector = Icons.Default.ArrowDropDown,
        contentDescription = "展开",
        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        modifier = Modifier.rotate(rotationState)
      )
    }

    // 模型列表下拉菜单
    AnimatedVisibility(
      visible = expanded,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically()
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          availableModels.forEachIndexed { index, model ->
            ModelItem(
              model = model,
              isSelected = model.id == selectedModel.id,
              onClick = {
                onModelSelected(model)
                expanded = false
              }
            )

            if (index < availableModels.size - 1) {
              HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
              )
            }
          }
        }
      }
    }
  }
}

/**
 * 模型列表项
 *
 * @param model 模型
 * @param isSelected 是否选中
 * @param onClick 点击回调
 */
@Composable
private fun ModelItem(
  model: AiModel,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .background(
        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        else Color.Transparent
      )
      .padding(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = model.name,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
          color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        if (model.description.isNotEmpty()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = model.description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
          )
        }
      }
    }
  }
} 